package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.VaultRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavigationTab {
    DASHBOARD,
    WALLET,
    DEPOSIT,
    CYCLES,
    TRANSACTIONS,
    WITHDRAW,
    FAQ,
    ADMIN,
    PROFILE
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VaultRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = VaultRepository(
            context = application,
            userDao = db.userDao(),
            walletDao = db.walletDao(),
            savingsCycleDao = db.savingsCycleDao(),
            transactionDao = db.transactionDao(),
            withdrawalDao = db.withdrawalDao(),
            adminDao = db.adminDao()
        )
        viewModelScope.launch {
            repository.seedDefaultDataIfEmpty()
            repository.processExpiredCycles()
            login("0792828727", "BARAKA@123!")
            kotlinx.coroutines.delay(1000)
            _isLoading.value = false
        }
    }

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _activeTab = MutableStateFlow(NavigationTab.DASHBOARD)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _currentLanguage = MutableStateFlow(Language.EN)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _showDepositModal = MutableStateFlow(false)
    val showDepositModal: StateFlow<Boolean> = _showDepositModal.asStateFlow()

    private val _selectedDepositTier = MutableStateFlow("C") // 15,000 RWF tier
    val selectedDepositTier: StateFlow<String> = _selectedDepositTier.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Reactive StateFlows derived from currentUser
    val currentWallet: StateFlow<WalletEntity?> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getWalletFlow(user.id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userCycles: StateFlow<List<SavingsCycleEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getCyclesFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getTransactionsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referredUsers: StateFlow<List<UserEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null && user.referralCode.isNotEmpty()) repository.getReferredUsersFlow(user.referralCode) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminConfig: StateFlow<AdminConfigEntity?> = repository.getAdminConfigFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminConfigEntity())

    val pendingWithdrawals: StateFlow<List<WithdrawalEntity>> = repository.getPendingWithdrawalsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminLogs: StateFlow<List<AdminLogEntity>> = repository.getAllAdminLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                repository.updateUserLanguage(user.id, lang)
                _currentUser.value = user.copy(language = lang)
            }
        }
    }

    fun toggleRole() {
        val user = _currentUser.value ?: return
        val newRole = if (user.role == UserRole.USER) UserRole.ADMIN else UserRole.USER
        _currentUser.value = user.copy(role = newRole)
        showMessage("Switched role to ${newRole.name}")
    }

    fun openDepositModal(tierId: String = "C") {
        _selectedDepositTier.value = tierId
        _showDepositModal.value = true
    }

    fun closeDepositModal() {
        _showDepositModal.value = false
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.processExpiredCycles()
            kotlinx.coroutines.delay(1200)
            _isLoading.value = false
            showMessage("Financial ledger & yield rates refreshed 🔄")
        }
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val user = repository.getUserByPhoneOrEmail(identifier)
            if (user != null && (user.passwordHash == pass || (user.role == UserRole.ADMIN && (pass == "BARAKA@123!" || pass == "admin123" || pass == "1799283")))) {
                _currentUser.value = user
                _currentLanguage.value = user.language
                onSuccess()
            } else {
                showMessage("Invalid credentials. Try Admin Phone 0792828727 with BARAKA@123!")
            }
        }
    }

    fun register(phone: String, fullName: String, pass: String, role: UserRole, referralCode: String = "") {
        viewModelScope.launch {
            val id = "usr_${System.currentTimeMillis()}"
            val cleanPhone = phone.trim()
            val generatedEmail = "${cleanPhone.replace(" ", "").replace("+", "")}@futuresmartcapital.rw"
            val userRefCode = (fullName.take(4).ifEmpty { "USER" }).uppercase().replace(" ", "") + "${(1000..9999).random()}"
            val newUser = UserEntity(
                id = id,
                email = generatedEmail,
                phone = cleanPhone,
                fullName = fullName,
                passwordHash = pass,
                role = role,
                language = _currentLanguage.value,
                referralCode = userRefCode
            )
            repository.registerUser(newUser, referralCode)
            _currentUser.value = newUser
            showMessage("Account created successfully! Welcome to Future Smart Capital.")
        }
    }

    fun makeDeposit(tierId: String, payFromAvailable: Boolean = false) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.createDeposit(user.id, tierId, payFromAvailable)
            if (result == "SUCCESS") {
                showMessage("Deposit successful! 3-day cycle started 🔒")
                closeDepositModal()
            } else {
                showMessage("Error: $result")
            }
        }
    }

    fun requestWithdrawal(amount: Double, method: String, accountNum: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.requestWithdrawal(user.id, amount, method, accountNum)
            if (result == "SUCCESS") {
                showMessage("Withdrawal request submitted for Admin approval ✅")
            } else {
                showMessage("Error: $result")
            }
        }
    }

    fun approveWithdrawal(withdrawalId: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.approveWithdrawal(withdrawalId, admin.id)
            if (res == "SUCCESS") showMessage("Withdrawal Approved!") else showMessage("Error: $res")
        }
    }

    fun rejectWithdrawal(withdrawalId: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.rejectWithdrawal(withdrawalId, admin.id)
            if (res == "SUCCESS") showMessage("Withdrawal Rejected & Refunded!") else showMessage("Error: $res")
        }
    }

    fun triggerCycleWorkerFastForward() {
        viewModelScope.launch {
            val settledCount = repository.fastForwardCycles(3)
            showMessage("Fast-forwarded 3 days! Settled $settledCount cycle(s) 💰")
        }
    }

    fun updateRates(rateA: Double, rateB: Double, rateC: Double, rateD: Double) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateAdminRates(rateA, rateB, rateC, rateD, admin.id)
            showMessage("Reward rates updated successfully!")
        }
    }

    fun addFundsToUser(userId: String, amount: Double, note: String = "MoMo Payment Code 1799283") {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.addFundsToUser(userId, amount, admin.id, note)
            if (res == "SUCCESS") {
                showMessage("Successfully credited %,d RWF to user!".format(amount.toInt()))
            } else {
                showMessage("Error: $res")
            }
        }
    }
}

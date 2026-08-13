package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.VaultRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.data.repository.CurrencyPreferencesRepository

enum class NavigationTab {
    DASHBOARD,
    WALLET,
    DEPOSIT,
    CYCLES,
    TRANSACTIONS,
    WITHDRAW,
    FAQ,
    ADMIN,
    PROFILE,
    WEB_DOWNLOAD
}

class VaultViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VaultRepository
    private val currencyPrefsRepo = CurrencyPreferencesRepository(application)

    val selectedCurrency: StateFlow<AppCurrency> = currencyPrefsRepo.selectedCurrencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCurrency.RWF)

    val isDarkMode: StateFlow<Boolean> = currencyPrefsRepo.isDarkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val currentLanguage: StateFlow<Language> = currencyPrefsRepo.selectedLanguageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Language.EN)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = VaultRepository(
            context = application,
            userDao = db.userDao(),
            walletDao = db.walletDao(),
            savingsCycleDao = db.savingsCycleDao(),
            transactionDao = db.transactionDao(),
            withdrawalDao = db.withdrawalDao(),
            depositRequestDao = db.depositRequestDao(),
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

    val pendingDepositRequests: StateFlow<List<DepositRequestEntity>> = repository.getPendingDepositRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDepositRequests: StateFlow<List<DepositRequestEntity>> = repository.getAllDepositRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminLogs: StateFlow<List<AdminLogEntity>> = repository.getAllAdminLogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun setLanguage(lang: Language) {
        viewModelScope.launch {
            currencyPrefsRepo.setLanguage(lang)
            val user = _currentUser.value
            if (user != null) {
                repository.updateUserLanguage(user.id, lang)
                _currentUser.value = user.copy(language = lang)
            }
            val msg = when (lang) {
                Language.EN -> "Language set to English 🇬🇧"
                Language.RW -> "Ururimi rwahinduwe mu Kinyarwanda 🇷🇼"
                Language.FR -> "Langue changée en Français 🇫🇷"
            }
            showMessage(msg)
        }
    }

    fun setCurrency(currency: AppCurrency) {
        viewModelScope.launch {
            currencyPrefsRepo.setCurrency(currency)
            showMessage("Currency changed to ${currency.flag} ${currency.code}")
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newMode = !isDarkMode.value
            currencyPrefsRepo.setDarkMode(newMode)
            showMessage(if (newMode) "Dark mode enabled 🌙" else "Light mode enabled ☀️")
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

    fun logout() {
        _currentUser.value = null
        _activeTab.value = NavigationTab.DASHBOARD
        showMessage("Logged out successfully.")
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val user = repository.getUserByPhoneOrEmail(identifier)
            if (user != null && (user.passwordHash == pass || (user.role == UserRole.ADMIN && (pass == "BARAKA@123!" || pass == "admin123" || pass == "1799283")))) {
                _currentUser.value = user
                currencyPrefsRepo.setLanguage(user.language)
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
                language = currentLanguage.value,
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

    fun submitDepositRequest(
        amount: Double,
        tierId: String,
        transactionId: String,
        proofScreenshotUri: String,
        paymentMethod: String = "MTN Mobile Money Code 1799283"
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.submitDepositRequest(
                userId = user.id,
                amount = amount,
                tierId = tierId,
                transactionId = transactionId,
                proofScreenshotUri = proofScreenshotUri,
                paymentMethod = paymentMethod
            )
            if (res == "SUCCESS") {
                closeDepositModal()
                showMessage("Deposit request submitted with Tx ID #$transactionId! Pending Admin Approval ⏳")
            } else {
                showMessage("Error: $res")
            }
        }
    }

    fun approveDepositRequest(requestId: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.approveDepositRequest(requestId, admin.id)
            if (res == "SUCCESS") {
                showMessage("Deposit Request Approved & Funds Credited! 💰")
            } else {
                showMessage("Error: $res")
            }
        }
    }

    fun rejectDepositRequest(requestId: String, note: String = "Deposit Verification Failed") {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.rejectDepositRequest(requestId, admin.id, note)
            if (res == "SUCCESS") {
                showMessage("Deposit Request Rejected")
            } else {
                showMessage("Error: $res")
            }
        }
    }

    fun addNewUserOrAdmin(
        fullName: String,
        phone: String,
        email: String,
        pass: String,
        role: UserRole,
        onSuccess: () -> Unit = {}
    ) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            val res = repository.addNewUserOrAdmin(fullName, phone, email, pass, role, admin.id)
            if (res == "SUCCESS") {
                showMessage("New ${role.name} '$fullName' created successfully! 🎉")
                onSuccess()
            } else {
                showMessage(res)
            }
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateUserRole(userId, newRole, admin.id)
            showMessage("User role updated to ${newRole.name}")
        }
    }
}

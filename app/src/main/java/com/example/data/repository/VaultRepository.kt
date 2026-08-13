package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class VaultRepository(
    private val context: android.content.Context,
    private val userDao: UserDao,
    private val walletDao: WalletDao,
    private val savingsCycleDao: SavingsCycleDao,
    private val transactionDao: TransactionDao,
    private val withdrawalDao: WithdrawalDao,
    private val depositRequestDao: DepositRequestDao,
    private val adminDao: AdminDao,
    private val announcementDao: AnnouncementDao
) {
    suspend fun seedDefaultDataIfEmpty() {
        // Seed admin config with 20M RWF System Reserve Fund
        var config = adminDao.getAdminConfig()
        if (config == null) {
            config = AdminConfigEntity(
                id = 1,
                rateTierA = 0.50,
                rateTierB = 0.50,
                rateTierC = 0.50,
                rateTierD = 0.50,
                cycleDurationDays = 3,
                isPlatformLocked = false,
                lockNotice = "SFC Platform deposits are temporarily scheduled for maintenance. Active savings cycles continue earning yields as normal!",
                adminReserveFund = 20000000.0 // 20 Million RWF Reserve Fund
            )
            adminDao.saveAdminConfig(config)
        } else if (config.adminReserveFund < 20000000.0) {
            adminDao.saveAdminConfig(config.copy(adminReserveFund = 20000000.0))
        }

        // Seed default announcement if empty
        val existingAnnouncements = announcementDao.getAllAnnouncementsFlow().firstOrNull()
        if (existingAnnouncements.isNullOrEmpty()) {
            announcementDao.insertAnnouncement(
                AnnouncementEntity(
                    id = "ann_sfc_welcome_001",
                    title = "Welcome to SMART FUTURE CAPITAL (SFC) 🚀",
                    content = "SMART FUTURE CAPITAL (SFC) official 3-day savings cycles are active! Deposit into Tier A, B, C, or D plans and earn automatic high-yield returns. System Liquidity Reserve is backed by 20,000,000 RWF.",
                    category = "IMPORTANT",
                    postedBy = "SFC System Admin",
                    timestamp = System.currentTimeMillis(),
                    isImportant = true
                )
            )
        }

        // Check if demo user exists
        val jean = userDao.getUserByEmail("jean@barakavault.rw")
        if (jean == null) {
            val jeanUser = UserEntity(
                id = "usr_jean_1001",
                email = "jean@barakavault.rw",
                phone = "+250 788 123 456",
                fullName = "Jean Nshimiyimana",
                passwordHash = "user123", // demo password
                role = UserRole.USER,
                language = Language.EN,
                referralCode = "JEAN2024"
            )
            userDao.insertUser(jeanUser)

            val jeanWallet = WalletEntity(
                userId = jeanUser.id,
                availableBalance = 12450.0,
                lockedBalance = 45000.0,
                totalEarned = 6750.0,
                totalDeposited = 70000.0,
                totalWithdrawn = 23300.0,
                referralBonus = 1250.0
            )
            walletDao.insertWallet(jeanWallet)

            // Active Cycle matching design mockup: 15,000 RWF Tier, Expected Reward 300 RWF
            val now = System.currentTimeMillis()
            val threeDaysMs = 3 * 24 * 60 * 60 * 1000L
            val activeCycle = SavingsCycleEntity(
                id = "#CY-982731",
                userId = jeanUser.id,
                tierId = "C",
                depositAmount = 15000.0,
                rate = 0.50,
                expectedReward = 7500.0,
                startDate = now - (2 * 24 * 60 * 60 * 1000L), // 2 days ago
                endDate = now + (1 * 24 * 60 * 60 * 1000L) + (23 * 3600 + 44 * 60) * 1000L, // ~1 day left
                status = CycleStatus.ACTIVE_LOCK
            )
            savingsCycleDao.insertCycle(activeCycle)

            val completedCycle = SavingsCycleEntity(
                id = "#CY-982100",
                userId = jeanUser.id,
                tierId = "D",
                depositAmount = 30000.0,
                rate = 0.50,
                expectedReward = 15000.0,
                startDate = now - (5 * 24 * 60 * 60 * 1000L),
                endDate = now - (2 * 24 * 60 * 60 * 1000L),
                status = CycleStatus.COMPLETED,
                settledAt = now - (2 * 24 * 60 * 60 * 1000L)
            )
            savingsCycleDao.insertCycle(completedCycle)

            // Seed sample transactions matching design screenshot
            val tx1 = TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = jeanUser.id,
                type = TransactionType.DEPOSIT,
                amount = -15000.0,
                description = "15,000 RWF Plan Deposit",
                status = TransactionStatus.LOCKED,
                timestamp = now - (2 * 24 * 3600 * 1000L),
                cycleId = "#CY-982731"
            )
            val tx2 = TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = jeanUser.id,
                type = TransactionType.CYCLE_REWARD,
                amount = 300.0,
                description = "Reward from Cycle #CY-982100",
                status = TransactionStatus.COMPLETED,
                timestamp = now - (3 * 24 * 3600 * 1000L),
                cycleId = "#CY-982100"
            )
            val tx3 = TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = jeanUser.id,
                type = TransactionType.WITHDRAWAL,
                amount = -5000.0,
                description = "Withdrawal to MTN Mobile Money (+250 788 123 456)",
                status = TransactionStatus.APPROVED,
                timestamp = now - (5 * 24 * 3600 * 1000L)
            )
            val tx4 = TransactionEntity(
                id = UUID.randomUUID().toString(),
                userId = jeanUser.id,
                type = TransactionType.DEPOSIT,
                amount = -10000.0,
                description = "10,000 RWF Plan Deposit",
                status = TransactionStatus.COMPLETED,
                timestamp = now - (6 * 24 * 3600 * 1000L)
            )
            transactionDao.insertTransaction(tx1)
            transactionDao.insertTransaction(tx2)
            transactionDao.insertTransaction(tx3)
            transactionDao.insertTransaction(tx4)
        }

        // Seed admin user (Phone: 0792828727, Pass: BARAKA@123!)
        val adminUser = UserEntity(
            id = "usr_admin_0792828727",
            email = "admin@futuresmartcapital.rw",
            phone = "0792828727",
            fullName = "FUTURE SMART CAPITAL Admin",
            passwordHash = "BARAKA@123!",
            role = UserRole.ADMIN,
            language = Language.EN,
            referralCode = "ADMIN"
        )
        userDao.insertUser(adminUser)

        val existingAdminWallet = walletDao.getWalletByUserId(adminUser.id)
        if (existingAdminWallet == null) {
            val adminWallet = WalletEntity(
                userId = adminUser.id,
                availableBalance = 1000000.0,
                lockedBalance = 0.0,
                totalEarned = 0.0,
                totalDeposited = 0.0,
                totalWithdrawn = 0.0
            )
            walletDao.insertWallet(adminWallet)
        }

        // Schedule local alarms for any active cycles
        scheduleAllActiveCycleNotifications()
    }

    suspend fun scheduleAllActiveCycleNotifications() {
        try {
            val activeCycles = savingsCycleDao.getActiveCycles()
            for (cycle in activeCycles) {
                com.example.notification.NotificationHelper.scheduleCycleExpirationNotification(
                    context = context,
                    cycleId = cycle.id,
                    depositAmount = cycle.depositAmount,
                    expectedReward = cycle.expectedReward,
                    triggerAtMillis = cycle.endDate
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("VaultRepository", "Error scheduling active cycle notifications: ${e.message}")
        }
    }

    // Auth & User Flow
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    suspend fun getUserByPhoneOrEmail(identifier: String): UserEntity? {
        val trimmed = identifier.trim()
        val directMatch = userDao.getUserByPhoneOrEmail(trimmed)
        if (directMatch != null) return directMatch

        val all = userDao.getAllUsers().firstOrNull() ?: emptyList()
        val cleanQuery = trimmed.replace(" ", "").replace("+250", "")
        return all.find { user ->
            val cleanUserPhone = user.phone.replace(" ", "").replace("+250", "")
            cleanUserPhone == cleanQuery || 
            user.email.equals(trimmed, ignoreCase = true) ||
            user.phone == trimmed ||
            (user.role == UserRole.ADMIN && (cleanQuery == "0792828727" || cleanQuery == "792828727"))
        }
    }
    fun getUserFlow(userId: String): Flow<UserEntity?> = userDao.getUserByIdFlow(userId)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getReferredUsersFlow(referralCode: String): Flow<List<UserEntity>> =
        userDao.getReferredUsersByCodeFlow(referralCode)

    fun getReferredCountFlow(referralCode: String): Flow<Int> =
        userDao.getReferredCountFlow(referralCode)

    suspend fun registerUser(user: UserEntity, referredByCode: String? = null) {
        val finalReferredBy = referredByCode?.trim()?.uppercase()
        val updatedUser = if (!finalReferredBy.isNullOrEmpty()) user.copy(referredBy = finalReferredBy) else user

        userDao.insertUser(updatedUser)
        walletDao.insertWallet(WalletEntity(userId = user.id, availableBalance = 0.0))

        // Process referral reward if referredByCode is valid
        if (!finalReferredBy.isNullOrEmpty()) {
            val referrer = userDao.getUserByReferralCode(finalReferredBy)
            if (referrer != null) {
                val referrerWallet = walletDao.getWalletByUserId(referrer.id)
                if (referrerWallet != null) {
                    val bonusAmount = 1000.0
                    val newBonus = referrerWallet.referralBonus + bonusAmount
                    val newAvailable = referrerWallet.availableBalance + bonusAmount
                    val newEarned = referrerWallet.totalEarned + bonusAmount

                    walletDao.updateWalletBalances(
                        userId = referrer.id,
                        available = newAvailable,
                        locked = referrerWallet.lockedBalance,
                        earned = newEarned,
                        deposited = referrerWallet.totalDeposited,
                        withdrawn = referrerWallet.totalWithdrawn,
                        referralBonus = newBonus
                    )

                    val bonusTx = TransactionEntity(
                        id = UUID.randomUUID().toString(),
                        userId = referrer.id,
                        type = TransactionType.REFERRAL_BONUS,
                        amount = bonusAmount,
                        description = "🎁 Referral Bonus for inviting ${user.fullName}",
                        status = TransactionStatus.COMPLETED,
                        timestamp = System.currentTimeMillis()
                    )
                    transactionDao.insertTransaction(bonusTx)
                }
            }
        }
    }

    suspend fun updateUserLanguage(userId: String, lang: Language) {
        userDao.updateUserLanguage(userId, lang)
    }

    // Wallet & Cycles
    fun getWalletFlow(userId: String): Flow<WalletEntity?> = walletDao.getWalletByUserIdFlow(userId)
    fun getCyclesFlow(userId: String): Flow<List<SavingsCycleEntity>> = savingsCycleDao.getCyclesByUserId(userId)
    fun getTransactionsFlow(userId: String): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByUserId(userId)
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getPendingWithdrawalsFlow(): Flow<List<WithdrawalEntity>> = withdrawalDao.getPendingWithdrawals()
    fun getAllWithdrawalsFlow(): Flow<List<WithdrawalEntity>> = withdrawalDao.getAllWithdrawals()
    fun getAdminConfigFlow(): Flow<AdminConfigEntity?> = adminDao.getAdminConfigFlow()
    fun getAllAdminLogsFlow(): Flow<List<AdminLogEntity>> = adminDao.getAllAdminLogs()

    // Core Cycle Engine: Process Expired Cycles (Settlement Worker)
    suspend fun processExpiredCycles(): Int {
        val now = System.currentTimeMillis()
        val activeCycles = savingsCycleDao.getActiveCycles()
        var settledCount = 0

        for (cycle in activeCycles) {
            if (cycle.endDate <= now) {
                // Idempotent check: Ensure cycle is still active
                val currentCycle = savingsCycleDao.getCycleById(cycle.id)
                if (currentCycle?.status == CycleStatus.ACTIVE_LOCK) {
                    val updatedCycle = currentCycle.copy(
                        status = CycleStatus.COMPLETED,
                        settledAt = now
                    )
                    savingsCycleDao.updateCycle(updatedCycle)

                    val wallet = walletDao.getWalletByUserId(cycle.userId)
                    if (wallet != null) {
                        val returnedDeposit = cycle.depositAmount
                        val reward = cycle.expectedReward
                        val newAvailable = wallet.availableBalance + returnedDeposit + reward
                        val newLocked = (wallet.lockedBalance - returnedDeposit).coerceAtLeast(0.0)
                        val newEarned = wallet.totalEarned + reward

                        walletDao.updateWalletBalances(
                            userId = wallet.userId,
                            available = newAvailable,
                            locked = newLocked,
                            earned = newEarned,
                            deposited = wallet.totalDeposited,
                            withdrawn = wallet.totalWithdrawn,
                            referralBonus = wallet.referralBonus
                        )

                        // Transaction log for settlement
                        val rewardTx = TransactionEntity(
                            id = UUID.randomUUID().toString(),
                            userId = cycle.userId,
                            type = TransactionType.CYCLE_REWARD,
                            amount = reward,
                            description = "Cycle Reward from ${cycle.id}",
                            status = TransactionStatus.COMPLETED,
                            timestamp = now,
                            cycleId = cycle.id
                        )
                        transactionDao.insertTransaction(rewardTx)

                        adminDao.insertAdminLog(
                            AdminLogEntity(
                                adminId = "SYSTEM_WORKER",
                                action = "CYCLE_SETTLED",
                                details = "Settled cycle ${cycle.id} for user ${cycle.userId}: +${reward} RWF reward"
                            )
                        )

                        // Trigger FCM / System Push Notification
                        try {
                            com.example.notification.NotificationHelper.sendRewardNotification(
                                context = context,
                                title = "🎉 50% Reward Released!",
                                message = "Your 3-day lock period for cycle ${cycle.id} has ended! +%,d RWF profit yield released to your wallet.".format(reward.toInt())
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("VaultRepository", "Notification error: ${e.message}")
                        }

                        settledCount++
                    }
                }
            }
        }
        return settledCount
    }

    // Fast Forward Simulator: Triggers cycle settlement instantly for testing
    suspend fun fastForwardCycles(days: Int = 3): Int {
        val activeCycles = savingsCycleDao.getActiveCycles()
        val shiftMs = days * 24 * 60 * 60 * 1000L
        for (cycle in activeCycles) {
            val shifted = cycle.copy(endDate = cycle.endDate - shiftMs)
            savingsCycleDao.updateCycle(shifted)
        }
        return processExpiredCycles()
    }

    // Deposit Flow
    suspend fun createDeposit(userId: String, tierId: String, payFromAvailable: Boolean = false): String {
        val config = adminDao.getAdminConfig() ?: AdminConfigEntity()
        val (amount, rate) = when (tierId.uppercase()) {
            "A" -> 6000.0 to config.rateTierA
            "B" -> 10000.0 to config.rateTierB
            "C" -> 15000.0 to config.rateTierC
            "D" -> 45000.0 to config.rateTierD
            else -> 6000.0 to config.rateTierA
        }

        val wallet = walletDao.getWalletByUserId(userId) ?: return "Wallet not found"

        if (payFromAvailable && wallet.availableBalance < amount) {
            return "Insufficient available balance"
        }

        val now = System.currentTimeMillis()
        val cycleDurationMs = config.cycleDurationDays * 24 * 60 * 60 * 1000L
        val expectedReward = amount * rate
        val cycleId = "#CY-${(100000..999999).random()}"

        val newCycle = SavingsCycleEntity(
            id = cycleId,
            userId = userId,
            tierId = tierId.uppercase(),
            depositAmount = amount,
            rate = rate,
            expectedReward = expectedReward,
            startDate = now,
            endDate = now + cycleDurationMs,
            status = CycleStatus.ACTIVE_LOCK
        )
        savingsCycleDao.insertCycle(newCycle)

        // Schedule Local Alarm Notification for when 3-day lock expires
        try {
            com.example.notification.NotificationHelper.scheduleCycleExpirationNotification(
                context = context,
                cycleId = cycleId,
                depositAmount = amount,
                expectedReward = expectedReward,
                triggerAtMillis = newCycle.endDate
            )
        } catch (e: Exception) {
            android.util.Log.e("VaultRepository", "Error scheduling local notification: ${e.message}")
        }

        val newAvailable = if (payFromAvailable) wallet.availableBalance - amount else wallet.availableBalance
        val newLocked = wallet.lockedBalance + amount
        val newDeposited = wallet.totalDeposited + amount

        walletDao.updateWalletBalances(
            userId = userId,
            available = newAvailable,
            locked = newLocked,
            earned = wallet.totalEarned,
            deposited = newDeposited,
            withdrawn = wallet.totalWithdrawn,
            referralBonus = wallet.referralBonus
        )

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = TransactionType.DEPOSIT,
            amount = -amount,
            description = "${amount.toInt()} RWF Plan Deposit ($cycleId)",
            status = TransactionStatus.LOCKED,
            timestamp = now,
            cycleId = cycleId
        )
        transactionDao.insertTransaction(tx)

        return "SUCCESS"
    }

    // Withdrawal Flow
    suspend fun requestWithdrawal(userId: String, amount: Double, method: String, accountNum: String): String {
        val wallet = walletDao.getWalletByUserId(userId) ?: return "Wallet not found"
        if (amount <= 0) return "Invalid amount"
        if (wallet.availableBalance < amount) return "Insufficient available balance"

        val now = System.currentTimeMillis()
        val withdrawalId = "WTH-${(100000..999999).random()}"

        val withdrawal = WithdrawalEntity(
            id = withdrawalId,
            userId = userId,
            amount = amount,
            payoutMethod = method,
            accountNumber = accountNum,
            status = TransactionStatus.PENDING,
            requestedAt = now
        )
        withdrawalDao.insertWithdrawal(withdrawal)

        // Hold balance pending admin approval
        val newAvailable = wallet.availableBalance - amount
        walletDao.updateWalletBalances(
            userId = userId,
            available = newAvailable,
            locked = wallet.lockedBalance,
            earned = wallet.totalEarned,
            deposited = wallet.totalDeposited,
            withdrawn = wallet.totalWithdrawn,
            referralBonus = wallet.referralBonus
        )

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = TransactionType.WITHDRAWAL,
            amount = -amount,
            description = "Withdrawal to $method ($accountNum)",
            status = TransactionStatus.PENDING,
            timestamp = now
        )
        transactionDao.insertTransaction(tx)

        return "SUCCESS"
    }

    suspend fun approveWithdrawal(withdrawalId: String, adminId: String): String {
        val withdrawals = withdrawalDao.getAllWithdrawals().firstOrNull() ?: emptyList()
        val withdrawal = withdrawals.find { it.id == withdrawalId } ?: return "Withdrawal not found"
        if (withdrawal.status != TransactionStatus.PENDING) return "Already processed"

        val now = System.currentTimeMillis()
        withdrawalDao.updateWithdrawalStatus(withdrawalId, TransactionStatus.APPROVED, now)

        val wallet = walletDao.getWalletByUserId(withdrawal.userId)
        if (wallet != null) {
            val newWithdrawn = wallet.totalWithdrawn + withdrawal.amount
            walletDao.updateWalletBalances(
                userId = wallet.userId,
                available = wallet.availableBalance,
                locked = wallet.lockedBalance,
                earned = wallet.totalEarned,
                deposited = wallet.totalDeposited,
                withdrawn = newWithdrawn,
                referralBonus = wallet.referralBonus
            )
        }

        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "WITHDRAWAL_APPROVED",
                details = "Approved withdrawal $withdrawalId of ${withdrawal.amount} RWF for user ${withdrawal.userId}"
            )
        )
        return "SUCCESS"
    }

    suspend fun rejectWithdrawal(withdrawalId: String, adminId: String): String {
        val withdrawals = withdrawalDao.getAllWithdrawals().firstOrNull() ?: emptyList()
        val withdrawal = withdrawals.find { it.id == withdrawalId } ?: return "Withdrawal not found"
        if (withdrawal.status != TransactionStatus.PENDING) return "Already processed"

        val now = System.currentTimeMillis()
        withdrawalDao.updateWithdrawalStatus(withdrawalId, TransactionStatus.REJECTED, now)

        // Refund held balance back to available balance
        val wallet = walletDao.getWalletByUserId(withdrawal.userId)
        if (wallet != null) {
            val newAvailable = wallet.availableBalance + withdrawal.amount
            walletDao.updateWalletBalances(
                userId = wallet.userId,
                available = newAvailable,
                locked = wallet.lockedBalance,
                earned = wallet.totalEarned,
                deposited = wallet.totalDeposited,
                withdrawn = wallet.totalWithdrawn,
                referralBonus = wallet.referralBonus
            )
        }

        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "WITHDRAWAL_REJECTED",
                details = "Rejected withdrawal $withdrawalId of ${withdrawal.amount} RWF for user ${withdrawal.userId}"
            )
        )
        return "SUCCESS"
    }

    // Admin Config & Management
    suspend fun updateAdminRates(rateA: Double, rateB: Double, rateC: Double, rateD: Double, adminId: String) {
        val config = AdminConfigEntity(
            id = 1,
            rateTierA = rateA,
            rateTierB = rateB,
            rateTierC = rateC,
            rateTierD = rateD,
            cycleDurationDays = 3
        )
        adminDao.saveAdminConfig(config)
        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "RATES_UPDATED",
                details = "Updated rates: Tier A=${rateA*100}%, Tier B=${rateB*100}%, Tier C=${rateC*100}%, Tier D=${rateD*100}%"
            )
        )
    }

    suspend fun addFundsToUser(userId: String, amount: Double, adminId: String, note: String = "MoMo Payment Approved"): String {
        val wallet = walletDao.getWalletByUserId(userId) ?: return "User wallet not found"
        val newAvailable = wallet.availableBalance + amount
        val newDeposited = wallet.totalDeposited + amount
        walletDao.updateWalletBalances(
            userId = userId,
            available = newAvailable,
            locked = wallet.lockedBalance,
            earned = wallet.totalEarned,
            deposited = newDeposited,
            withdrawn = wallet.totalWithdrawn,
            referralBonus = wallet.referralBonus
        )

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = TransactionType.DEPOSIT,
            amount = amount,
            description = "Admin Approved Deposit ($note)",
            status = TransactionStatus.APPROVED,
            timestamp = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(tx)

        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "FUNDS_ADDED",
                details = "Added %,d RWF to user $userId ($note)".format(amount.toInt())
            )
        )
        return "SUCCESS"
    }

    // Deposit Requests Flow
    fun getPendingDepositRequests(): Flow<List<DepositRequestEntity>> =
        depositRequestDao.getPendingDepositRequests()

    fun getAllDepositRequests(): Flow<List<DepositRequestEntity>> =
        depositRequestDao.getAllDepositRequests()

    fun getDepositRequestsByUserId(userId: String): Flow<List<DepositRequestEntity>> =
        depositRequestDao.getDepositRequestsByUserId(userId)

    suspend fun submitDepositRequest(
        userId: String,
        amount: Double,
        tierId: String,
        transactionId: String,
        proofScreenshotUri: String,
        paymentMethod: String = "MTN Mobile Money Code 1799283"
    ): String {
        if (amount <= 0) return "Invalid deposit amount"
        if (transactionId.isBlank()) return "Transaction ID is required"

        val requestId = "DEP-${(100000..999999).random()}"
        val request = DepositRequestEntity(
            id = requestId,
            userId = userId,
            amount = amount,
            tierId = tierId,
            transactionId = transactionId.trim(),
            proofScreenshotUri = proofScreenshotUri,
            paymentMethod = paymentMethod,
            status = TransactionStatus.PENDING,
            requestedAt = System.currentTimeMillis()
        )
        depositRequestDao.insertDepositRequest(request)

        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = TransactionType.DEPOSIT,
            amount = amount,
            description = "Deposit Request #$requestId (Tx: $transactionId)",
            status = TransactionStatus.PENDING,
            timestamp = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(tx)

        return "SUCCESS"
    }

    suspend fun approveDepositRequest(requestId: String, adminId: String): String {
        val req = depositRequestDao.getDepositRequestById(requestId) ?: return "Deposit request not found"
        if (req.status != TransactionStatus.PENDING) return "Deposit request already processed"

        val now = System.currentTimeMillis()
        depositRequestDao.updateDepositRequestStatus(requestId, TransactionStatus.APPROVED, now, "Approved by Admin $adminId")

        // Add funds to user wallet
        val res = addFundsToUser(
            userId = req.userId,
            amount = req.amount,
            adminId = adminId,
            note = "Approved Deposit #$requestId (Tx: ${req.transactionId})"
        )

        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "DEPOSIT_APPROVED",
                details = "Approved deposit request $requestId for user ${req.userId} (Amount: %,d RWF, Tx: ${req.transactionId})".format(req.amount.toInt())
            )
        )
        return res
    }

    suspend fun rejectDepositRequest(requestId: String, adminId: String, note: String = "Deposit Verification Failed"): String {
        val req = depositRequestDao.getDepositRequestById(requestId) ?: return "Deposit request not found"
        if (req.status != TransactionStatus.PENDING) return "Deposit request already processed"

        val now = System.currentTimeMillis()
        depositRequestDao.updateDepositRequestStatus(requestId, TransactionStatus.REJECTED, now, note)

        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "DEPOSIT_REJECTED",
                details = "Rejected deposit request $requestId for user ${req.userId} ($note)"
            )
        )
        return "SUCCESS"
    }

    // User & Admin Management
    suspend fun addNewUserOrAdmin(
        fullName: String,
        phone: String,
        email: String,
        pass: String,
        role: UserRole,
        adminId: String
    ): String {
        if (fullName.isBlank() || phone.isBlank() || pass.isBlank()) {
            return "Please fill in all required fields"
        }

        val existingPhone = userDao.getUserByPhoneOrEmail(phone)
        if (existingPhone != null) {
            return "A user with this phone number already exists"
        }

        val effectiveEmail = if (email.isBlank()) "user_${System.currentTimeMillis()}@barakavault.rw" else email
        val existingEmail = userDao.getUserByEmail(effectiveEmail)
        if (existingEmail != null) {
            return "A user with this email address already exists"
        }

        val userId = if (role == UserRole.ADMIN) "admin_${(1000..9999).random()}" else "usr_${(100000..999999).random()}"
        val refCode = (fullName.take(3) + (1000..9999).random()).uppercase()

        val newUser = UserEntity(
            id = userId,
            email = effectiveEmail,
            phone = phone,
            fullName = fullName,
            passwordHash = pass,
            role = role,
            language = Language.EN,
            referralCode = refCode
        )
        userDao.insertUser(newUser)

        val newWallet = WalletEntity(
            userId = userId,
            availableBalance = 0.0,
            lockedBalance = 0.0,
            totalEarned = 0.0,
            totalDeposited = 0.0,
            totalWithdrawn = 0.0,
            referralBonus = 0.0
        )
        walletDao.insertWallet(newWallet)

        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = if (role == UserRole.ADMIN) "ADMIN_CREATED" else "USER_CREATED",
                details = "Created new ${role.name}: $fullName ($phone, ID: $userId)"
            )
        )

        return "SUCCESS"
    }

    suspend fun updateUserRole(userId: String, newRole: UserRole, adminId: String) {
        userDao.updateUserRole(userId, newRole)
        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "ROLE_UPDATED",
                details = "Updated user $userId role to ${newRole.name}"
            )
        )
    }

    // Announcements & News Methods
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncementsFlow()

    suspend fun postAnnouncement(
        title: String,
        content: String,
        category: String = "NEWS",
        isImportant: Boolean = false,
        adminId: String = "admin"
    ) {
        val announcement = AnnouncementEntity(
            id = "ann_${UUID.randomUUID().toString().take(8)}",
            title = title,
            content = content,
            category = category,
            postedBy = "SFC Admin ($adminId)",
            timestamp = System.currentTimeMillis(),
            isImportant = isImportant
        )
        announcementDao.insertAnnouncement(announcement)
        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "ANNOUNCEMENT_POSTED",
                details = "Posted announcement: $title ($category)"
            )
        )
    }

    suspend fun deleteAnnouncement(id: String, adminId: String) {
        announcementDao.deleteAnnouncement(id)
        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "ANNOUNCEMENT_DELETED",
                details = "Deleted announcement ID: $id"
            )
        )
    }

    // Platform Lock & Lock Schedule
    suspend fun togglePlatformLock(isLocked: Boolean, lockNotice: String, adminId: String) {
        val current = adminDao.getAdminConfig() ?: AdminConfigEntity()
        val updated = current.copy(
            isPlatformLocked = isLocked,
            lockNotice = if (lockNotice.isBlank()) current.lockNotice else lockNotice
        )
        adminDao.saveAdminConfig(updated)
        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = if (isLocked) "PLATFORM_LOCKED" else "PLATFORM_UNLOCKED",
                details = "Set platform deposit status to ${if (isLocked) "LOCKED" else "OPEN"}. Notice: $lockNotice"
            )
        )
    }

    // Admin Reserve Fund Management (20M RWF)
    suspend fun updateAdminReserveFund(newReserveAmount: Double, adminId: String) {
        val current = adminDao.getAdminConfig() ?: AdminConfigEntity()
        val updated = current.copy(adminReserveFund = newReserveAmount)
        adminDao.saveAdminConfig(updated)
        adminDao.insertAdminLog(
            AdminLogEntity(
                adminId = adminId,
                action = "RESERVE_FUND_UPDATED",
                details = "Updated System Capital Reserve to $newReserveAmount RWF"
            )
        )
    }
}

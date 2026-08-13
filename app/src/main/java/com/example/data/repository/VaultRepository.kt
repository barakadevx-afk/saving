package com.example.data.repository

import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class VaultRepository(
    private val userDao: UserDao,
    private val walletDao: WalletDao,
    private val savingsCycleDao: SavingsCycleDao,
    private val transactionDao: TransactionDao,
    private val withdrawalDao: WithdrawalDao,
    private val adminDao: AdminDao
) {
    suspend fun seedDefaultDataIfEmpty() {
        // Seed admin config
        var config = adminDao.getAdminConfig()
        if (config == null) {
            config = AdminConfigEntity(
                id = 1,
                rateTierA = 0.02,
                rateTierB = 0.02,
                rateTierC = 0.02,
                rateTierD = 0.02,
                cycleDurationDays = 3
            )
            adminDao.saveAdminConfig(config)
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
                rate = 0.02,
                expectedReward = 300.0,
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
                rate = 0.02,
                expectedReward = 600.0,
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

        // Seed admin user
        val admin = userDao.getUserByEmail("admin@barakavault.rw")
        if (admin == null) {
            val adminUser = UserEntity(
                id = "usr_admin_001",
                email = "admin@barakavault.rw",
                phone = "+250 788 000 000",
                fullName = "Baraka System Admin",
                passwordHash = "admin123",
                role = UserRole.ADMIN,
                language = Language.EN,
                referralCode = "ADMIN"
            )
            userDao.insertUser(adminUser)

            val adminWallet = WalletEntity(
                userId = adminUser.id,
                availableBalance = 500000.0,
                lockedBalance = 0.0,
                totalEarned = 0.0,
                totalDeposited = 0.0,
                totalWithdrawn = 0.0
            )
            walletDao.insertWallet(adminWallet)
        }
    }

    // Auth & User Flow
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    fun getUserFlow(userId: String): Flow<UserEntity?> = userDao.getUserByIdFlow(userId)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsers()
    suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
        walletDao.insertWallet(WalletEntity(userId = user.id, availableBalance = 0.0))
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
                            withdrawn = wallet.totalWithdrawn
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

        val newAvailable = if (payFromAvailable) wallet.availableBalance - amount else wallet.availableBalance
        val newLocked = wallet.lockedBalance + amount
        val newDeposited = wallet.totalDeposited + amount

        walletDao.updateWalletBalances(
            userId = userId,
            available = newAvailable,
            locked = newLocked,
            earned = wallet.totalEarned,
            deposited = newDeposited,
            withdrawn = wallet.totalWithdrawn
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
            withdrawn = wallet.totalWithdrawn
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
                withdrawn = newWithdrawn
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
                withdrawn = wallet.totalWithdrawn
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
}

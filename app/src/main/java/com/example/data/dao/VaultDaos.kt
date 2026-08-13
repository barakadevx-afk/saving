package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET language = :lang WHERE id = :userId")
    suspend fun updateUserLanguage(userId: String, lang: Language)

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    suspend fun updateUserRole(userId: String, role: UserRole)
}

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    fun getWalletByUserIdFlow(userId: String): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets WHERE userId = :userId LIMIT 1")
    suspend fun getWalletByUserId(userId: String): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Query("UPDATE wallets SET availableBalance = :available, lockedBalance = :locked, totalEarned = :earned, totalDeposited = :deposited, totalWithdrawn = :withdrawn WHERE userId = :userId")
    suspend fun updateWalletBalances(
        userId: String,
        available: Double,
        locked: Double,
        earned: Double,
        deposited: Double,
        withdrawn: Double
    )
}

@Dao
interface SavingsCycleDao {
    @Query("SELECT * FROM savings_cycles WHERE userId = :userId ORDER BY startDate DESC")
    fun getCyclesByUserId(userId: String): Flow<List<SavingsCycleEntity>>

    @Query("SELECT * FROM savings_cycles WHERE status = 'ACTIVE_LOCK'")
    suspend fun getActiveCycles(): List<SavingsCycleEntity>

    @Query("SELECT * FROM savings_cycles WHERE id = :id LIMIT 1")
    suspend fun getCycleById(id: String): SavingsCycleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: SavingsCycleEntity)

    @Update
    suspend fun updateCycle(cycle: SavingsCycleEntity)

    @Query("SELECT COUNT(*) FROM savings_cycles WHERE userId = :userId AND status = 'ACTIVE_LOCK'")
    fun getActiveCycleCount(userId: String): Flow<Int>
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 50")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)
}

@Dao
interface WithdrawalDao {
    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getWithdrawalsByUserId(userId: String): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals ORDER BY requestedAt DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Query("SELECT * FROM withdrawals WHERE status = 'PENDING' ORDER BY requestedAt DESC")
    fun getPendingWithdrawals(): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)

    @Query("UPDATE withdrawals SET status = :status, processedAt = :processedAt WHERE id = :id")
    suspend fun updateWithdrawalStatus(id: String, status: TransactionStatus, processedAt: Long)
}

@Dao
interface AdminDao {
    @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
    fun getAdminConfigFlow(): Flow<AdminConfigEntity?>

    @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
    suspend fun getAdminConfig(): AdminConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAdminConfig(config: AdminConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminLog(log: AdminLogEntity)

    @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllAdminLogs(): Flow<List<AdminLogEntity>>
}

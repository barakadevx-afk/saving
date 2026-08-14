package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :identifier OR email = :identifier OR REPLACE(phone, ' ', '') = REPLACE(:identifier, ' ', '') LIMIT 1")
    suspend fun getUserByPhoneOrEmail(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE referredBy = :referralCode ORDER BY createdAt DESC")
    fun getReferredUsersByCodeFlow(referralCode: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE referredBy = :referralCode")
    suspend fun getReferredUsersListByCode(referralCode: String): List<UserEntity>

    @Query("SELECT COUNT(*) FROM users WHERE referredBy = :referralCode")
    fun getReferredCountFlow(referralCode: String): Flow<Int>

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

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

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("UPDATE wallets SET availableBalance = :available, lockedBalance = :locked, totalEarned = :earned, totalDeposited = :deposited, totalWithdrawn = :withdrawn, referralBonus = :referralBonus, pendingBonusPercent = :pendingBonusPercent, totalFriendsReferred = :totalFriendsReferred WHERE userId = :userId")
    suspend fun updateWalletBalances(
        userId: String,
        available: Double,
        locked: Double,
        earned: Double,
        deposited: Double,
        withdrawn: Double,
        referralBonus: Double,
        pendingBonusPercent: Double = 0.0,
        totalFriendsReferred: Int = 0
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
interface DepositRequestDao {
    @Query("SELECT * FROM deposit_requests WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getDepositRequestsByUserId(userId: String): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposit_requests ORDER BY requestedAt DESC")
    fun getAllDepositRequests(): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposit_requests WHERE status = 'PENDING' ORDER BY requestedAt DESC")
    fun getPendingDepositRequests(): Flow<List<DepositRequestEntity>>

    @Query("SELECT * FROM deposit_requests WHERE id = :id LIMIT 1")
    suspend fun getDepositRequestById(id: String): DepositRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepositRequest(request: DepositRequestEntity)

    @Query("UPDATE deposit_requests SET status = :status, processedAt = :processedAt, adminNote = :note WHERE id = :id")
    suspend fun updateDepositRequestStatus(id: String, status: TransactionStatus, processedAt: Long, note: String? = null)
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

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)
}

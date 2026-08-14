package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole { USER, ADMIN }
enum class Language { EN, RW, FR }

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val phone: String,
    val fullName: String,
    val passwordHash: String,
    val role: UserRole = UserRole.USER,
    val language: Language = Language.EN,
    val referralCode: String = "",
    val referredBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val userId: String,
    val availableBalance: Double = 0.0,
    val lockedBalance: Double = 0.0,
    val totalEarned: Double = 0.0,
    val totalDeposited: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val referralBonus: Double = 0.0,
    val pendingBonusPercent: Double = 0.0, // Extra bonus percentage boost for the next cycle (e.g. 0.005 for +0.5%)
    val totalFriendsReferred: Int = 0,
    val hasClaimedWelcomeBonus: Boolean = false
)

enum class CycleStatus { ACTIVE_LOCK, COMPLETED, AVAILABLE }

@Entity(tableName = "savings_cycles")
data class SavingsCycleEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val tierId: String, // "A" (6,000), "B" (10,000), "C" (15,000), "D" (45,000)
    val depositAmount: Double,
    val rate: Double, // e.g., 0.02 = 2.0%
    val expectedReward: Double,
    val startDate: Long,
    val endDate: Long,
    val status: CycleStatus = CycleStatus.ACTIVE_LOCK,
    val settledAt: Long? = null,
    val bonusRateApplied: Double = 0.0 // Extra yield percentage earned via referral bonus boost
)

enum class TransactionType { DEPOSIT, CYCLE_REWARD, WITHDRAWAL, ADMIN_ADJUSTMENT, REFERRAL_BONUS, WELCOME_BONUS }
enum class TransactionStatus { LOCKED, COMPLETED, APPROVED, PENDING, REJECTED }

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val status: TransactionStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val cycleId: String? = null
)

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val payoutMethod: String, // e.g. "MTN Mobile Money", "Airtel Money", "Bank Account"
    val accountNumber: String,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)

@Entity(tableName = "deposit_requests")
data class DepositRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val amount: Double,
    val tierId: String = "CUSTOM", // "A", "B", "C", "D", or "CUSTOM"
    val transactionId: String,
    val proofScreenshotUri: String = "",
    val paymentMethod: String = "MTN Mobile Money Code 1799283",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null,
    val adminNote: String? = null
)

@Entity(tableName = "admin_config")
data class AdminConfigEntity(
    @PrimaryKey val id: Int = 1,
    val rateTierA: Double = 0.02, // 6,000 RWF -> 120 RWF (2.0%)
    val rateTierB: Double = 0.02, // 10,000 RWF -> 200 RWF (2.0%)
    val rateTierC: Double = 0.02, // 15,000 RWF -> 300 RWF (2.0%)
    val rateTierD: Double = 0.02, // 45,000 RWF -> 900 RWF (2.0%)
    val cycleDurationDays: Int = 3,
    val isPlatformLocked: Boolean = false,
    val lockNotice: String = "SFC Platform deposits are temporarily scheduled for maintenance. Active savings cycles continue earning yields as normal!",
    val adminReserveFund: Double = 20000000.0
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val category: String = "NEWS", // "NEWS", "URGENT", "MAINTENANCE", "PROMO"
    val postedBy: String = "SFC Admin",
    val timestamp: Long = System.currentTimeMillis(),
    val isImportant: Boolean = false,
    val imageUrl: String? = null
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminId: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

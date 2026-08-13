package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        WalletEntity::class,
        SavingsCycleEntity::class,
        TransactionEntity::class,
        WithdrawalEntity::class,
        DepositRequestEntity::class,
        AdminConfigEntity::class,
        AdminLogEntity::class,
        AnnouncementEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walletDao(): WalletDao
    abstract fun savingsCycleDao(): SavingsCycleDao
    abstract fun transactionDao(): TransactionDao
    abstract fun withdrawalDao(): WithdrawalDao
    abstract fun depositRequestDao(): DepositRequestDao
    abstract fun adminDao(): AdminDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "baraka_vault_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

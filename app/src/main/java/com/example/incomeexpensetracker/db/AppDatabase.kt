package com.example.incomeexpensetracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.incomeexpensetracker.dao.CategoryDao
import com.example.incomeexpensetracker.dao.SubcategoryDao
import com.example.incomeexpensetracker.dao.TransactionDao
import com.example.incomeexpensetracker.transactions.CategoryEntity
import com.example.incomeexpensetracker.transactions.SubcategoryEntity
import com.example.incomeexpensetracker.transactions.TransactionEntity

@Database(entities = [CategoryEntity::class, SubcategoryEntity::class, TransactionEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun subcategoryDao(): SubcategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Define the migration for version 1 -> 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add description column to the "transactions" table
                database.execSQL("ALTER TABLE transactions ADD COLUMN description TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "income_expense_tracker"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
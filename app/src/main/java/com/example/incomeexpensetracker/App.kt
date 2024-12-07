package com.example.incomeexpensetracker

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.db.AppDatabase.Companion.getDatabase
import com.example.incomeexpensetracker.transactions.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the database with population
        prepopulateDatabase()
    }

    private fun prepopulateDatabase() {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "income_expense_tracker"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate categories (Income, Expense)
                CoroutineScope(Dispatchers.IO).launch {
                    getDatabase(applicationContext).categoryDao()
                        .insertCategory(CategoryEntity(type = "Income"))
                    getDatabase(applicationContext).categoryDao()
                        .insertCategory(CategoryEntity(type = "Expense"))
                }
            }
        }).build()
    }
}
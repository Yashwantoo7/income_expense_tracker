package com.example.incomeexpensetracker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class App : Application() {

    private val TAG: String = "App.kt"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "App.kt onCreate")
        // Initialize the database with population
        prepopulateDatabase()
    }

    private fun prepopulateDatabase() {
        val dbFile = applicationContext.getDatabasePath("income_expense_tracker")
        Log.d(TAG, "Database file path: ${dbFile.absolutePath}")

        // Check if the database already exists
        if (!dbFile.exists()) {
            Log.d(TAG, "Database does not exist. Creating and prepopulating data.")
            // Create the database and insert the data
            val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "income_expense_tracker"
            ).fallbackToDestructiveMigration() // This ensures a fresh start on migration changes
                .addCallback(object : androidx.room.RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d(TAG, "Database created for the first time.")
                    }
                }).build()

            // Log the database creation
            Log.d(TAG, "Database is being created.")
            // Insert initial data
            CoroutineScope(Dispatchers.IO).launch {
                val appDatabase = AppDatabase.getDatabase(applicationContext)
                appDatabase.categoryDao().insertCategory(CategoryEntity(type = "Income"))
                appDatabase.categoryDao().insertCategory(CategoryEntity(type = "Expense"))
            }
        } else {
            Log.d(TAG, "Database already exists. No need to prepopulate.")
//            deleteDatabase(applicationContext)
        }
    }

    fun deleteDatabase(context: Context) {
        val dbFile = context.getDatabasePath("income_expense_tracker")
        if (dbFile.exists()) {
            val deleted = context.deleteDatabase("income_expense_tracker")
            if (deleted) {
                Log.d("App.kt", "Database deleted successfully.")
            } else {
                Log.d("App.kt", "Failed to delete the database.")
            }
        } else {
            Log.d("App.kt", "Database does not exist.")
        }
    }
}

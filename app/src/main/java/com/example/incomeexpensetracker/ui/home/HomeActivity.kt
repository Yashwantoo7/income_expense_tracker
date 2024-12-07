package com.example.incomeexpensetracker.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.incomeexpensetracker.App
import com.example.incomeexpensetracker.databinding.ActivityHomeBinding
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.CategoryEntity
import com.example.incomeexpensetracker.ui.addedit.AddEditTransactionActivity
import com.example.incomeexpensetracker.ui.summary.SummaryActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val TAG = "HomeActivity.kt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIncome.setOnClickListener {
            val intent = Intent(this, AddEditTransactionActivity::class.java).apply {
                putExtra("CATEGORY_TYPE", "Income")
            }
            startActivity(intent)
        }

        binding.btnExpense.setOnClickListener {
            val intent = Intent(this, AddEditTransactionActivity::class.java).apply {
                putExtra("CATEGORY_TYPE", "Expense")
            }
            startActivity(intent)
        }

        binding.btnSummary.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        binding.btnDeleteDb.setOnClickListener {
            // Deleting the database using the application context
            val dbName = "income_expense_tracker"  // Replace with your actual database name
            applicationContext.deleteDatabase(dbName)
            // Optional: Log or show a message to confirm deletion
            println("Database deleted.")

            CoroutineScope(Dispatchers.IO).launch {
                // Force a new instance of the database to be created
                val appDatabase = AppDatabase.getDatabase(applicationContext)
                appDatabase.clearAllTables()
                appDatabase.categoryDao().insertCategory(CategoryEntity(type = "Income"))
                appDatabase.categoryDao().insertCategory(CategoryEntity(type = "Expense"))
                Log.d(TAG, "Database instance cleared and recreated.")
            }
        }
    }
}

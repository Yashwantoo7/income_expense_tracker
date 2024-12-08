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
    }
}

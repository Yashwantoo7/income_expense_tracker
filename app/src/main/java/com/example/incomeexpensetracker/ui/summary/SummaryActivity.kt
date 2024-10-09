package com.example.incomeexpensetracker.ui.summary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.databinding.ActivitySummaryBinding
import com.example.incomeexpensetracker.ui.summary.adapter.RecentTransactionsAdapter
import com.example.incomeexpensetracker.ui.summary.adapter.Transaction

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding
    private lateinit var adapter: RecentTransactionsAdapter
    private val recentTransactions = mutableListOf<Transaction>() // Replace with your Transaction model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecentTransactionsAdapter(recentTransactions)
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvRecentTransactions.adapter = adapter

        // Load summary data from database
        loadSummaryData()
    }

    private fun loadSummaryData() {
        // TODO: Fetch total income, total expense, and recent transactions from Room database
        // For demonstration, setting dummy data
        binding.tvTotalIncome.text = "Total Income: \$5000.00"
        binding.tvTotalExpense.text = "Total Expense: \$3000.00"
        binding.tvBalance.text = "Balance: \$2000.00"

        // Adding dummy recent transactions
        recentTransactions.addAll(
            listOf(
                Transaction(1, 100.0, "Income", "Salary", System.currentTimeMillis()),
                Transaction(2, 50.0, "Expense", "Food", System.currentTimeMillis())
            )
        )
        adapter.notifyDataSetChanged()
    }
}

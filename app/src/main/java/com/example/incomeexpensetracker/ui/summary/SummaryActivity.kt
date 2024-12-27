package com.example.incomeexpensetracker.ui.summary

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.R
import com.example.incomeexpensetracker.databinding.ActivitySummaryBinding
import com.example.incomeexpensetracker.ui.summary.adapter.RecentTransactionsAdapter
import com.example.incomeexpensetracker.ui.summary.adapter.Transaction
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SummaryActivity : AppCompatActivity() {

    private val TAG: String = "SummaryActivity.kt"

    private lateinit var binding: ActivitySummaryBinding
    private lateinit var adapter: RecentTransactionsAdapter
    private val recentTransactions = mutableListOf<Transaction>() // Replace with your Transaction model

    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView for recent transactions
        adapter = RecentTransactionsAdapter(recentTransactions)
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvRecentTransactions.adapter = adapter

        // Observe the total income, total expense, and balance
        observeSummaryData()

        // Observe recent transactions
        observeRecentTransactions()
    }

    private fun observeSummaryData() {
        lifecycleScope.launchWhenStarted {
            launch {
                transactionViewModel.totalIncome.collect { income ->
                    binding.tvTotalIncome.text = "Total Income: Rs. ${income ?: 0.0}"
                    // Extract numeric value from tvTotalIncome and pass to updateBalance
                    val totalIncomeValue = extractAmount(binding.tvTotalIncome.text.toString())
                    updateBalance(totalIncomeValue, extractAmount(binding.tvTotalExpense.text.toString()))
                }
            }
            launch {
                transactionViewModel.totalExpense.collect { expense ->
                    binding.tvTotalExpense.text = "Total Expense: Rs. ${expense ?: 0.0}"
                    // Extract numeric value from tvTotalExpense and pass to updateBalance
                    val totalExpenseValue = extractAmount(binding.tvTotalExpense.text.toString())
                    updateBalance(extractAmount(binding.tvTotalIncome.text.toString()), totalExpenseValue)
                }
            }
        }
    }

    // Helper function to extract numeric value from text
    private fun extractAmount(text: String): Double {
        // Remove non-numeric characters except the decimal point
        var numericString = text.replace("[^\\d.]".toRegex(), "")

        // If the string starts with a dot (like ".100.0"), remove it
        if (numericString.startsWith(".")) {
            numericString = numericString.substring(1)
        }

        // Ensure only one decimal point is present
        val parts = numericString.split(".")
        if (parts.size > 2) {
            // Keep only the first part and the first decimal point
            numericString = "${parts[0]}.${parts[1]}"
        }

        Log.d(TAG, "Extracted numeric string: $numericString, from text: $text")
        return numericString.toDoubleOrNull() ?: 0.0
    }



    private fun updateBalance(income: Double, expense: Double) {
        val balance = income - expense
        binding.tvBalance.text = "Balance: Rs. $balance"
        if (balance>0) {
            binding.tvBalance.setTextColor(binding.root.context.getColor(R.color.green))
        } else {
            binding.tvBalance.setTextColor(binding.root.context.getColor(R.color.red))
        }
    }

    private fun observeRecentTransactions() {
        lifecycleScope.launchWhenStarted {
            transactionViewModel.recentTransactions.collect { transactions ->
                // Map the transactions to your UI model (Transaction)
                recentTransactions.clear()
                recentTransactions.addAll(transactions.map {
                    Transaction(it.id, it.amount, it.category, it.subcategory, it.date, it.description)
                })
                adapter.notifyDataSetChanged()
            }
        }
    }
}

package com.example.incomeexpensetracker.ui.summary

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.databinding.ActivitySummaryBinding
import com.example.incomeexpensetracker.ui.summary.adapter.RecentTransactionsAdapter
import com.example.incomeexpensetracker.ui.summary.adapter.Transaction
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SummaryActivity : AppCompatActivity() {

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
                    binding.tvTotalIncome.text = "Total Income: $${income ?: 0.0}"
                    // Extract numeric value from tvTotalIncome and pass to updateBalance
                    val totalIncomeValue = extractAmount(binding.tvTotalIncome.text.toString())
                    updateBalance(totalIncomeValue, extractAmount(binding.tvTotalExpense.text.toString()))
                }
            }
            launch {
                transactionViewModel.totalExpense.collect { expense ->
                    binding.tvTotalExpense.text = "Total Expense: $${expense ?: 0.0}"
                    // Extract numeric value from tvTotalExpense and pass to updateBalance
                    val totalExpenseValue = extractAmount(binding.tvTotalExpense.text.toString())
                    updateBalance(extractAmount(binding.tvTotalIncome.text.toString()), totalExpenseValue)
                }
            }
        }
    }

    // Helper function to extract numeric value from text
    private fun extractAmount(text: String): Double {
        // Remove the non-numeric characters like "$" and "Rs."
        val numericString = text.replace("[^0-9.]".toRegex(), "")
        return numericString.toDoubleOrNull() ?: 0.0
    }

    private fun updateBalance(income: Double, expense: Double) {
        val balance = income - expense
        binding.tvBalance.text = "Balance: Rs. $balance"
    }

    private fun observeRecentTransactions() {
        lifecycleScope.launchWhenStarted {
            transactionViewModel.recentTransactions.collect { transactions ->
                // Map the transactions to your UI model (Transaction)
                recentTransactions.clear()
                recentTransactions.addAll(transactions.map {
                    Transaction(it.id, it.amount, it.category, it.subcategory, it.date)
                })
                adapter.notifyDataSetChanged()
            }
        }
    }
}

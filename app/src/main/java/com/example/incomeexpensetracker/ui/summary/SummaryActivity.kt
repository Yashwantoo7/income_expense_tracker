package com.example.incomeexpensetracker.ui.summary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.R
import com.example.incomeexpensetracker.databinding.ActivitySummaryBinding
import com.example.incomeexpensetracker.ui.summary.adapter.RecentTransactionsAdapter
import com.example.incomeexpensetracker.ui.summary.adapter.Transaction
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import com.example.incomeexpensetracker.ui.addedit.EditTransactionActivity
import com.example.incomeexpensetracker.utils.TransactionFilterHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding
    private lateinit var adapter: RecentTransactionsAdapter
    private val recentTransactions = mutableListOf<Transaction>()
    private lateinit var transactionFilterHelper: TransactionFilterHelper
    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView for recent transactions
        adapter = RecentTransactionsAdapter(this, recentTransactions, onEditClick = {
            onEditTransactionClick(it)
        }, onDeleteClick = {
            lifecycleScope.launch {
                transactionViewModel.deleteTransaction(it)
            }
        })

        transactionFilterHelper = TransactionFilterHelper()

        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvRecentTransactions.adapter = adapter

        // Observe the total income, total expense, and balance
        observeSummaryData(-1)

        // Populate the Spinner for days selection
        // Get the Spinner for selecting days
        val daysSpinner: Spinner = binding.daysSpinner
        setupDaysSpinner(daysSpinner)

        // Observe recent transactions based on selected number of days
        observeRecentTransactions(-1)
    }

    private fun setupDaysSpinner(daysSpinner: Spinner) {
        daysSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                val selectedDays = parentView.getItemAtPosition(position).toString()
                if (selectedDays=="All") {
                    observeRecentTransactions(-1)
                    observeSummaryData(-1)
                } else {
                    observeRecentTransactions(selectedDays.toInt())  // Fetch transactions for selected days
                    observeSummaryData(selectedDays.toInt())
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case when no item is selected (default to last 30 days)
                observeRecentTransactions(30)
            }
        }
    }

    private fun observeRecentTransactions(days: Int) {
        lifecycleScope.launch {
            if (days != -1) {
                val filteredTransactions = transactionFilterHelper.filterTransactionsByDays(transactionViewModel.recentTransactions, days)
                filteredTransactions.collect { transactions ->
                    // Map the transactions to your UI model (Transaction)
                    recentTransactions.clear()
                    recentTransactions.addAll(transactions.map {
                        Transaction(it.id, it.amount, it.category, it.subcategory, it.date, it.description)
                    })
                    adapter.notifyDataSetChanged()
                }
            } else {
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

    private fun observeSummaryData(days: Int) {
        lifecycleScope.launch {
            if (days != -1) {
                launch {
                    val filteredTransactions = transactionFilterHelper.filterTransactionsByDays(transactionViewModel.recentTransactions, days)
                    filteredTransactions.collect {transactions->
                        val income = transactions.filter { it.category == "Income" }.sumOf { it.amount }
                        val expense = transactions.filter { it.category == "Expense" }.sumOf { it.amount }
                        binding.tvTotalIncome.text = "Total Income: Rs. ${income ?: 0.0}"
                        // Extract numeric value from tvTotalIncome and pass to updateBalance
                        val totalIncomeValue = extractAmount(binding.tvTotalIncome.text.toString())
                        updateBalance(totalIncomeValue, extractAmount(binding.tvTotalExpense.text.toString()))
                        binding.tvTotalExpense.text = "Total Expense: Rs. ${expense ?: 0.0}"
                        // Extract numeric value from tvTotalExpense and pass to updateBalance
                        val totalExpenseValue = extractAmount(binding.tvTotalExpense.text.toString())
                        updateBalance(extractAmount(binding.tvTotalIncome.text.toString()), totalExpenseValue)
                    }
                }
            } else {
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


    private fun onEditTransactionClick(transaction: Transaction) {
        // Launch an edit dialog or activity
        val intent = Intent(this, EditTransactionActivity::class.java).apply {
            putExtra("transaction_id", transaction.id)
        }
        startActivity(intent)
    }
}

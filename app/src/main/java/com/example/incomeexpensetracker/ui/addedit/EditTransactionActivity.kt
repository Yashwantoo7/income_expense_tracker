package com.example.incomeexpensetracker.ui.addedit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensetracker.databinding.ActivityEditTransactionBinding
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import com.example.incomeexpensetracker.transactions.TransactionEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTransactionBinding
    private var transactionId: Int = -1
    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get transaction ID from intent
        transactionId = intent.getIntExtra("transaction_id", -1)

        // Populate fields if editing an existing transaction
        if (transactionId != -1) {
            populateTransactionDetails(transactionId)
        }

        // Set up category spinner
        val categories = listOf("Income", "Expense")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // Handle save button click
        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction()
        }
    }

    private fun populateTransactionDetails(transactionId: Int) {
        lifecycleScope.launch {
            val transaction =
                transactionViewModel.getTransactionById(transactionId) // Replace with your implementation
            if (transaction != null) {
                binding.etAmount.setText(transaction.amount.toString())
                binding.spinnerCategory.setSelection(if (transaction.category == "Income") 0 else 1)
                binding.etSubcategory.setText(transaction.subcategory)
                binding.etDate.setText(
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date(transaction.date))
                )
                binding.etDescription.setText(transaction.description)
            }
        }
    }

    private fun saveTransaction() {
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val category = binding.spinnerCategory.selectedItem.toString()
        val subcategory = binding.etSubcategory.text.toString()
        val date = binding.etDate.text.toString()
        val description = binding.etDescription.text.toString()

        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val parsedDate = sdf.parse(date)?.time ?: System.currentTimeMillis()

        val updatedTransaction = TransactionEntity(
            id = transactionId,
            amount = amount,
            category = category,
            subcategory = subcategory,
            date = parsedDate,
            description = description
        )

        lifecycleScope.launch {
            // Update transaction in the ViewModel
            transactionViewModel.updateTransaction(updatedTransaction) // Replace with your implementation
            finish() // Close the activity
        }
    }
}

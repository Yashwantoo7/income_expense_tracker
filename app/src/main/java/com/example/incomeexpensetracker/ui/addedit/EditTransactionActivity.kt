package com.example.incomeexpensetracker.ui.addedit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensetracker.databinding.ActivityEditTransactionBinding
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import com.example.incomeexpensetracker.mvvm.CategoryViewModel
import com.example.incomeexpensetracker.transactions.TransactionEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTransactionBinding
    private var transactionId: Int = -1
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

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

        // Listen for category selection changes
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update subcategories based on selected category
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                setupSubcategorySpinner(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Handle save button click
        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction()
        }
    }

    private fun populateTransactionDetails(transactionId: Int) {
        lifecycleScope.launch {
            val transaction = transactionViewModel.getTransactionById(transactionId) // Fetch transaction

            if (transaction != null) {
                binding.etAmount.setText(transaction.amount.toString())

                // Format and set the date
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(transaction.date)) // Format the date from milliseconds
                binding.etDate.setText(formattedDate) // Set the formatted date into the EditText

                // Set the description
                binding.etDescription.setText(transaction.description) // Set the description

                // Set the category selection
                val selectedCategory = if (transaction.category == "Income") 0 else 1
                binding.spinnerCategory.setSelection(selectedCategory)

                // Populate subcategories based on the category (Income or Expense)
                setupSubcategorySpinner(transaction.category)

                // Wait until the subcategory spinner is populated before setting the selection
                categoryViewModel.getSubcategoriesByCategory(if (transaction.category == "Income") 0 else 1).collect { subcategories ->
                    val subcategoryNames = subcategories.map { it.name }
                    val subcategoryPosition = subcategoryNames.indexOf(transaction.subcategory)

                    // Set the selected subcategory in the spinner
                    binding.spinnerSubcategory.setSelection(subcategoryPosition)
                }
            }
        }
    }

    private fun setupSubcategorySpinner(category: String) {
        lifecycleScope.launch {
            categoryViewModel.getCategoryIdByType(category).collect { categoryId ->
                categoryViewModel.getSubcategoriesByCategory(categoryId).collect { subcategories ->
                    val subcategoryNames = subcategories.map { it.name }

                    val subcategoryAdapter = ArrayAdapter(
                        this@EditTransactionActivity,
                        android.R.layout.simple_spinner_item,
                        subcategoryNames
                    )
                    subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSubcategory.adapter = subcategoryAdapter

                    binding.spinnerSubcategory.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                // Store the selected subcategory if needed
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                }
            }
        }
    }

    private fun saveTransaction() {
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val category = binding.spinnerCategory.selectedItem.toString()
        val subcategory = binding.spinnerSubcategory.selectedItem.toString()
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
            transactionViewModel.updateTransaction(updatedTransaction)
            finish() // Close the activity
        }
    }
}
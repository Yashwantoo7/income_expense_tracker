package com.example.incomeexpensetracker.ui.addedit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.incomeexpensetracker.databinding.ActivityAddEditTransactionBinding
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.db.DatabaseLogger
import com.example.incomeexpensetracker.mvvm.CategoryViewModel
import com.example.incomeexpensetracker.mvvm.TransactionViewModel
import com.example.incomeexpensetracker.transactions.TransactionEntity
import com.example.incomeexpensetracker.ui.subcategory.SubcategoryActivity // Adjust import as per your package structure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTransactionBinding
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()

    private var categoryType: String = "Income"
    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedSubcategory: String = ""
    private var selectedCategory: String = ""

//    private val incomeSubcategories = listOf("Salary", "Freelance", "Trading")
//    private val expenseSubcategories = listOf("Food", "Travel", "Cloths")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get category type from intent
        categoryType = intent.getStringExtra("CATEGORY_TYPE") ?: "Income"
        binding.tvCategoryType.text = categoryType

        // Setup category spinner
        val categories = listOf("Income", "Expense")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        // Set initial selection based on categoryType
        val categoryIndex = categories.indexOf(categoryType)
        if (categoryIndex >= 0) {
            binding.spinnerCategory.setSelection(categoryIndex)
        }

        // Setup subcategory spinner based on categoryType
        setupSubcategorySpinner(categoryType)

        DatabaseLogger.logAllEntries(AppDatabase.getDatabase(this))

        // Handle category change
        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategory = parent.getItemAtPosition(position).toString()
                    setupSubcategorySpinner(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Handle if nothing is selected, if necessary
                }
            }

        // Date picker
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Save transaction
        binding.btnSaveTransaction.setOnClickListener {
            // Handle save logic
            // You can collect data from input fields and save to database
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            if (amount != null && selectedSubcategory.isNotEmpty()) {
                val transactionEntity = TransactionEntity(
                    amount = amount,
                    category = selectedCategory,
                    subcategory = selectedSubcategory,
                    date = selectedDate
                )

                // Save transaction to the database using TransactionViewModel
                lifecycleScope.launch {
                    transactionViewModel.saveTransaction(transactionEntity)
                    // Return to the previous activity after saving the transaction
                    finish()
                }
            } else {
                // Show a toast or error message if the data is invalid
                binding.etAmount.error = "Please enter a valid amount"
            }
        }

        // Button to manage subcategories
        binding.btnManageSubcategories.setOnClickListener {
            // Start SubCategoryActivity
            val intent = Intent(this, SubcategoryActivity::class.java)
            startActivity(intent)
        }

        // Button to manage subcategories
        binding.btnManageSubcategories.setOnClickListener {
            // Start SubCategoryActivity
            val intent = Intent(this, SubcategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSubcategorySpinner(category: String) {
        lifecycleScope.launch {
            // Fetch categoryId from ViewModel based on category type
            // Assuming categoryViewModel is already providing this functionality
            categoryViewModel.getCategoryIdByType(category).collect { categoryId ->
                // Now fetch subcategories using categoryId
                categoryViewModel.getSubcategoriesByCategory(categoryId).collect { subcategories ->
                    val subcategoryNames = subcategories.map { it.name }
                    val subcategoryAdapter = ArrayAdapter(
                        this@AddEditTransactionActivity,
                        android.R.layout.simple_spinner_item,
                        subcategoryNames
                    )
                    subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSubcategory.adapter = subcategoryAdapter

                    // Set item selected listener for subcategory spinner
                    binding.spinnerSubcategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            // Fetch the selected subcategory id
                            selectedSubcategory = subcategories[position].name
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // Handle case when no subcategory is selected
                        }
                    }

                    // Store selected category
                    selectedCategory = category
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = calendar.timeInMillis
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvDate.text = sdf.format(Date(selectedDate))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }
}
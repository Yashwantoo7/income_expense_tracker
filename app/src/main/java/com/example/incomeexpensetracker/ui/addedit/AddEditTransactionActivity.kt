package com.example.incomeexpensetracker.ui.addedit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get category type from intent
        categoryType = intent.getStringExtra("CATEGORY_TYPE") ?: "Income"
        binding.tvCategoryType.text = categoryType

        // Setup category spinner with color customization
        setupCategorySpinner()

        // Setup subcategory spinner based on categoryType
        setupSubcategorySpinner(categoryType)

        // Log database entries (for debugging)
        DatabaseLogger.logAllEntries(AppDatabase.getDatabase(this))

        // Date picker
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Save transaction
        binding.btnSaveTransaction.setOnClickListener {
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            val description = binding.etNotes.text.toString()

            if (amount != null && selectedSubcategory.isNotEmpty()) {
                val transactionEntity = TransactionEntity(
                    amount = amount,
                    category = selectedCategory,
                    subcategory = selectedSubcategory,
                    date = selectedDate,
                    description = description
                )

                lifecycleScope.launch {
                    transactionViewModel.saveTransaction(transactionEntity)
                    finish() // Close the activity after saving
                }
            } else {
                binding.etAmount.error = "Please enter a valid amount"
            }
        }

        // Manage subcategories button
        binding.btnManageSubcategories.setOnClickListener {
            val intent = Intent(this, SubcategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Income", "Expense")

        // Custom ArrayAdapter to change text color based on category type
        val categoryAdapter = object : ArrayAdapter<String>(
            this@AddEditTransactionActivity,
            android.R.layout.simple_spinner_item,
            categories
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(
                    if (categories[position] == "Income") getColor(android.R.color.holo_green_dark)
                    else getColor(android.R.color.holo_red_dark)
                )
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(
                    if (categories[position] == "Income") getColor(android.R.color.holo_green_dark)
                    else getColor(android.R.color.holo_red_dark)
                )
                return view
            }
        }

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        // Set initial selection based on categoryType
        val categoryIndex = categories.indexOf(categoryType)
        if (categoryIndex >= 0) {
            binding.spinnerCategory.setSelection(categoryIndex)
        }

        // Handle category change
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                setupSubcategorySpinner(selectedCategory) // Update subcategories based on selection
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupSubcategorySpinner(category: String) {
        lifecycleScope.launch {
            categoryViewModel.getCategoryIdByType(category).collect { categoryId ->
                categoryViewModel.getSubcategoriesByCategory(categoryId).collect { subcategories ->
                    val subcategoryNames = subcategories.map { it.name }
                    val subcategoryAdapter = ArrayAdapter(
                        this@AddEditTransactionActivity,
                        android.R.layout.simple_spinner_item,
                        subcategoryNames
                    )
                    subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spinnerSubcategory.adapter = subcategoryAdapter

                    binding.spinnerSubcategory.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedSubcategory = subcategories[position].name
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }
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

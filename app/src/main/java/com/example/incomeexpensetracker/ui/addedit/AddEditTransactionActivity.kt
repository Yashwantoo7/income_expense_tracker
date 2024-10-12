package com.example.incomeexpensetracker.ui.addedit

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.incomeexpensetracker.databinding.ActivityAddEditTransactionBinding
import com.example.incomeexpensetracker.ui.subcategory.SubcategoryActivity // Adjust import as per your package structure
import java.text.SimpleDateFormat
import java.util.*

class AddEditTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTransactionBinding
    private var categoryType: String = "Income"
    private var selectedDate: Long = System.currentTimeMillis()

    private val incomeSubcategories = listOf("Salary", "Freelance", "Trading")
    private val expenseSubcategories = listOf("Food", "Travel", "Cloths")

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

        // Handle category change
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
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
        }

        // Button to manage subcategories
        binding.btnManageSubcategories.setOnClickListener {
            // Start SubCategoryActivity
            val intent = Intent(this, SubcategoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSubcategorySpinner(category: String) {
        val subcategories = when (category) {
            "Income" -> incomeSubcategories
            "Expense" -> expenseSubcategories
            else -> listOf()
        }

        val subcategoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subcategories)
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubcategory.adapter = subcategoryAdapter
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
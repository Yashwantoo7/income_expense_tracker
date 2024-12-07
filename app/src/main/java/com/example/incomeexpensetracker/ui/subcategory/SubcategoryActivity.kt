package com.example.incomeexpensetracker.ui.subcategory

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.databinding.ActivitySubcategoryBinding
import com.example.incomeexpensetracker.databinding.DialogAddSubcategoryBinding
import com.example.incomeexpensetracker.mvvm.CategoryViewModel
import com.example.incomeexpensetracker.ui.subcategory.adapter.SubcategoryAdapter
import com.example.incomeexpensetracker.mvvm.SubcategoryViewModel
import com.example.incomeexpensetracker.transactions.SubcategoryEntity
import kotlinx.coroutines.launch

class SubcategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubcategoryBinding
    private lateinit var adapter: SubcategoryAdapter
    private val categoryViewModel: CategoryViewModel by viewModels()
    private val subcategoryViewModel: SubcategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding for activity_subcategory.xml
        binding = ActivitySubcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView with LinearLayoutManager
        adapter = SubcategoryAdapter(emptyList()) { subcategory ->
            deleteSubcategory(subcategory)
        }
        binding.rvSubcategories.layoutManager = LinearLayoutManager(this)
        binding.rvSubcategories.adapter = adapter

        // Observe subcategories from the ViewModel (Flow)
        lifecycleScope.launch {
            subcategoryViewModel.allSubcategories.collect { subcategories ->
                // Update the list in the adapter when the data changes
                adapter.updateList(subcategories)
            }
        }

        // Floating action button to add a new subcategory
        binding.fabAddSubcategory.setOnClickListener {
            showAddSubcategoryDialog()
        }
    }

    // Function to show the dialog for adding a new subcategory
    private fun showAddSubcategoryDialog() {
        // Initialize ViewBinding for dialog_add_subcategory.xml
        val dialogBinding = DialogAddSubcategoryBinding.inflate(layoutInflater)

        // Get the list of categories from the database or ViewModel
        lifecycleScope.launch {
            categoryViewModel.allCategories.collect { categories ->
                val categoryNames = categories.map { it.type } // Map category entities to names
                val categoryAdapter = ArrayAdapter(
                    this@SubcategoryActivity,
                    R.layout.simple_spinner_item,
                    categoryNames
                )
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dialogBinding.spinnerCategory.adapter = categoryAdapter

                // Set listener for category selection
                dialogBinding.spinnerCategory.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            val selectedCategory = categories[position]
                            dialogBinding.btnAddSubcategory.setOnClickListener {
                                val subcategoryName = dialogBinding.etSubcategoryName.text.toString()
                                if (subcategoryName.isNotEmpty()) {
                                    addSubcategory(subcategoryName, selectedCategory.id)
                                }
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
            }
        }

        // Create AlertDialog for input
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Subcategory")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Function to add a subcategory
    private fun addSubcategory(subcategoryName: String, categoryId: Int) {
        val subcategory = SubcategoryEntity(name = subcategoryName, categoryId = categoryId) // Set categoryId as needed
        subcategoryViewModel.addSubcategory(subcategory)
    }

    // Function to delete a subcategory
    private fun deleteSubcategory(subcategory: SubcategoryEntity) {
        subcategoryViewModel.deleteSubcategory(subcategory)
    }
}

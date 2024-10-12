package com.example.incomeexpensetracker.ui.subcategory

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.databinding.ActivitySubcategoryBinding
import com.example.incomeexpensetracker.databinding.DialogAddSubcategoryBinding
import com.example.incomeexpensetracker.ui.subcategory.adapter.SubcategoryAdapter

class SubcategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubcategoryBinding
    private lateinit var adapter: SubcategoryAdapter
    private val subcategories = mutableListOf<String>() // Holds the list of subcategories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding for activity_subcategory.xml
        binding = ActivitySubcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView with LinearLayoutManager
        adapter = SubcategoryAdapter(subcategories) { subcategory ->
            deleteSubcategory(subcategory)
        }
        binding.rvSubcategories.layoutManager = LinearLayoutManager(this)
        binding.rvSubcategories.adapter = adapter

        // Floating action button to add a new subcategory
        binding.fabAddSubcategory.setOnClickListener {
            showAddSubcategoryDialog()
        }
    }

    // Function to show the dialog for adding a new subcategory
    private fun showAddSubcategoryDialog() {
        // Initialize ViewBinding for dialog_add_subcategory.xml
        val dialogBinding = DialogAddSubcategoryBinding.inflate(layoutInflater)

        // Create AlertDialog for input
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Subcategory")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val subcategoryName = dialogBinding.etSubcategoryName.text.toString()
                if (subcategoryName.isNotEmpty()) {
                    addSubcategory(subcategoryName)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Function to add a subcategory
    private fun addSubcategory(subcategory: String) {
        subcategories.add(subcategory)
        adapter.notifyDataSetChanged()
    }

    // Function to delete a subcategory
    private fun deleteSubcategory(subcategory: String) {
        subcategories.remove(subcategory)
        adapter.notifyDataSetChanged()
    }
}
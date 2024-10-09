package com.example.incomeexpensetracker.ui.subcategory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incomeexpensetracker.databinding.ActivitySubcategoryBinding
import com.example.incomeexpensetracker.ui.subcategory.adapter.SubcategoryAdapter

class SubcategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubcategoryBinding
    private lateinit var adapter: SubcategoryAdapter
    private val subcategories = mutableListOf<String>() // Fetch from database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubcategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SubcategoryAdapter(subcategories)
        binding.rvSubcategories.layoutManager = LinearLayoutManager(this)
        binding.rvSubcategories.adapter = adapter

        // Load subcategories from database
        loadSubcategories()

        binding.btnAddSubcategory.setOnClickListener {
            // Show dialog to add subcategory
            // On positive action, add to database and update list
        }
    }

    private fun loadSubcategories() {
        // TODO: Fetch subcategories from Room database and update 'subcategories' list
        // For demonstration, adding dummy data
        subcategories.addAll(listOf("Food", "Travel", "Cloths", "Salary", "Freelance"))
        adapter.notifyDataSetChanged()
    }
}

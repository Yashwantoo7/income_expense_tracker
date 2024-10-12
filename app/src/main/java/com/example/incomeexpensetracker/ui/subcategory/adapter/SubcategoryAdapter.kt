package com.example.incomeexpensetracker.ui.subcategory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensetracker.databinding.ItemSubcategoryBinding

class SubcategoryAdapter(
    private val subcategories: List<String>,
    private val onDeleteClick: (String) -> Unit // Lambda function to handle delete clicks
) : RecyclerView.Adapter<SubcategoryAdapter.SubCategoryViewHolder>() {

    inner class SubCategoryViewHolder(val binding: ItemSubcategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val binding = ItemSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        val subcategory = subcategories[position]
        holder.binding.tvSubcategoryName.text = subcategory

        // Set delete button click listener
        holder.binding.btnDeleteSubcategory.setOnClickListener {
            onDeleteClick(subcategory)
        }
    }

    override fun getItemCount(): Int {
        return subcategories.size
    }
}
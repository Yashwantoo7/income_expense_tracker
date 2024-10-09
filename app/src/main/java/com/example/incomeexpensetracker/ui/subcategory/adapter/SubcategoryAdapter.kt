package com.example.incomeexpensetracker.ui.subcategory.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensetracker.databinding.ItemSubcategoryBinding

class SubcategoryAdapter(private val subcategories: List<String>) :
    RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val binding = ItemSubcategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubcategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.bind(subcategories[position])
    }

    override fun getItemCount(): Int = subcategories.size

    inner class SubcategoryViewHolder(private val binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subcategory: String) {
            binding.tvSubcategoryName.text = subcategory
            // Handle edit/delete actions if needed
        }
    }
}

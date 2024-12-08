package com.example.incomeexpensetracker.ui.subcategory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensetracker.databinding.ItemSubcategoryBinding
import com.example.incomeexpensetracker.transactions.SubcategoryEntity

class SubcategoryAdapter(
    private var subcategories: List<SubcategoryEntity>,
    private val onDeleteSubcategory: (SubcategoryEntity) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

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

    fun updateList(newSubcategories: List<SubcategoryEntity>) {
        subcategories = newSubcategories
        notifyDataSetChanged()
    }

    inner class SubcategoryViewHolder(private val binding: ItemSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subcategory: SubcategoryEntity) {
            binding.tvSubcategoryName.text = subcategory.name
            binding.btnDeleteSubcategory.setOnClickListener {
                onDeleteSubcategory(subcategory)
            }
        }
    }
}

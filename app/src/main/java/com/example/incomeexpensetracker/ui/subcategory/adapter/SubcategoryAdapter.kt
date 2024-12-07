package com.example.incomeexpensetracker.ui.subcategory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensetracker.R
import com.example.incomeexpensetracker.transactions.SubcategoryEntity

class SubcategoryAdapter(
    private var subcategories: List<SubcategoryEntity>,
    private val onDeleteClick: (SubcategoryEntity) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subcategory, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        val subcategory = subcategories[position]
        holder.bind(subcategory)
    }

    override fun getItemCount(): Int {
        return subcategories.size
    }

    fun updateList(newSubcategories: List<SubcategoryEntity>) {
        subcategories = newSubcategories
        notifyDataSetChanged()
    }

    inner class SubcategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val subcategoryName: TextView = view.findViewById(R.id.tvSubcategoryName)

        fun bind(subcategory: SubcategoryEntity) {
            subcategoryName.text = subcategory.name

            itemView.setOnLongClickListener {
                onDeleteClick(subcategory)
                true
            }
        }
    }
}

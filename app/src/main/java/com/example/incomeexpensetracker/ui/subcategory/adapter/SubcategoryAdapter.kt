package com.example.incomeexpensetracker.ui.subcategory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensetracker.R
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
                showDeleteConfirmationDialog(it.context, subcategory)
            }
        }

        private fun showDeleteConfirmationDialog(context: Context, subcategory: SubcategoryEntity) {
            val dialog = AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this subcategory?")
                .setPositiveButton("Delete") { _, _ ->
                    // If the user confirms, call the delete function
                    onDeleteSubcategory(subcategory)
                }
                .setNegativeButton("Cancel", null) // Dismiss the dialog on cancel
                .create()

            // Customize button colors
            dialog.setOnShowListener {
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(binding.root.context.getColor(
                    R.color.red))
                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(binding.root.context.getColor(
                    R.color.gray))
            }
            dialog.show()
        }
    }
}

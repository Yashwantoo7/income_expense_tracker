package com.example.incomeexpensetracker.ui.summary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.incomeexpensetracker.R
import com.example.incomeexpensetracker.databinding.ItemRecentTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val id: Int,
    val amount: Double,
    val category: String,
    val subcategory: String,
    val date: Long
)

class RecentTransactionsAdapter(private val transactions: MutableList<Transaction>) :
    RecyclerView.Adapter<RecentTransactionsAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemRecentTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(private val binding: ItemRecentTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvAmount.text = if (transaction.category == "Income") {
                "+\$${transaction.amount}"
            } else {
                "-\$${transaction.amount}"
            }

            binding.tvSubcategory.text = transaction.subcategory

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(transaction.date))

            // Optionally, set text color based on category
            if (transaction.category == "Income") {
                binding.tvAmount.setTextColor(binding.root.context.getColor(R.color.black))
            } else {
                binding.tvAmount.setTextColor(binding.root.context.getColor(R.color.black))
            }
        }
    }
}

package com.example.incomeexpensetracker.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incomeexpensetracker.dao.TransactionDao
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.TransactionEntity
import com.example.incomeexpensetracker.ui.summary.adapter.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao: TransactionDao = AppDatabase.getDatabase(application).transactionDao()

    // Flow to get recent transactions
    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.getRecentTransactions()

    // Flow to get total income
    val totalIncome: Flow<Double> = transactionDao.getTotalIncome()

    // Flow to get total expense
    val totalExpense: Flow<Double> = transactionDao.getTotalExpense()

    // Method to get a transaction by ID
    suspend fun getTransactionById(transactionId: Int): TransactionEntity? {
        return transactionDao.getTransactionById(transactionId)
    }

    // Method to delete transaction
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    //Method to save a transaction to a database
    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }

    //Method to update transaction in database
    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.updateTransaction(transaction)
        }
    }
}
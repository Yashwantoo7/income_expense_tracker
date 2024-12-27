package com.example.incomeexpensetracker.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incomeexpensetracker.dao.TransactionDao
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.TransactionEntity
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

    //Method to save a transaction to a database
    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }
}
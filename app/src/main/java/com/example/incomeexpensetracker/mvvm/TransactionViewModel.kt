package com.example.incomeexpensetracker.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incomeexpensetracker.dao.TransactionDao
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.TransactionEntity
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao: TransactionDao = AppDatabase.getDatabase(application).transactionDao()

    //Method to save a transaction to a database
    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }
}
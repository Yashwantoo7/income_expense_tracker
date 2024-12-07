package com.example.incomeexpensetracker.db

import android.util.Log
import com.example.incomeexpensetracker.dao.CategoryDao
import com.example.incomeexpensetracker.dao.SubcategoryDao
import com.example.incomeexpensetracker.dao.TransactionDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object DatabaseLogger {

    private const val TAG: String = "DatabaseLogger"

    // Method to log all categories
    private fun logAllCategories(categoryDao: CategoryDao) {
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                categoryDao.getAllCategories().collect { categories ->
                    categories.forEach {
                        Log.d("DatabaseLogger", "Category: ${it.type}")
                    }
                }
            }
        }
    }

    // Method to log all subcategories
    private fun logAllSubcategories(subcategoryDao: SubcategoryDao) {
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                subcategoryDao.getAllSubcategories().collect { subcategories ->
                    subcategories.forEach {
                        Log.d("DatabaseLogger", "Subcategory: ${it.name} | Category ID: ${it.categoryId}")
                    }
                }
            }
        }
    }

    // Method to log all transactions
    private fun logAllTransactions(transactionDao: TransactionDao) {
        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                transactionDao.getAllTransactions().collect { transactions ->
                    transactions.forEach {
                        Log.d("DatabaseLogger", "Transaction: Amount: ${it.amount}, Category: ${it.category}, Subcategory: ${it.subcategory}, Date: ${it.date}")
                    }
                }
            }
        }
    }

    // Method to log all entities (for easy logging of all tables)
    fun logAllEntries(appDatabase: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Logging all entities...")
            logAllCategories(appDatabase.categoryDao())
            logAllSubcategories(appDatabase.subcategoryDao())
            logAllTransactions(appDatabase.transactionDao())
            // Add additional entities here if you have more tables
        }
    }
}

package com.example.incomeexpensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.incomeexpensetracker.transactions.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getRecentTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): TransactionEntity?

    @Query("SELECT SUM(amount) FROM transactions WHERE category = 'Income'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT SUM(amount) FROM transactions WHERE category = 'Expense'")
    fun getTotalExpense(): Flow<Double>

    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :keyword || '%' ORDER BY date DESC")
    fun getTransactionsByDescription(keyword: String): Flow<List<TransactionEntity>>
}
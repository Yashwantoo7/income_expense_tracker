package com.example.incomeexpensetracker.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.incomeexpensetracker.transactions.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type")
    suspend fun getCategoryByType(type: String): CategoryEntity?

    // Get categoryId by category type (Income/Expense)
    @Query("SELECT id FROM categories WHERE type = :type LIMIT 1")
    fun getCategoryIdByType(type: String): Flow<Int>
}
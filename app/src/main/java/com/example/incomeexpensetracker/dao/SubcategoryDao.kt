package com.example.incomeexpensetracker.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.incomeexpensetracker.transactions.SubcategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao {
    @Insert
    suspend fun insertSubcategory(subcategory: SubcategoryEntity)

    @Delete
    suspend fun deleteSubcategory(subcategory: SubcategoryEntity)

    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId")
    fun getSubcategoriesByCategory(categoryId: Int): Flow<List<SubcategoryEntity>>

    @Query("SELECT * FROM subcategories")
    fun getAllSubcategories(): Flow<List<SubcategoryEntity>>
}
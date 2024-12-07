package com.example.incomeexpensetracker.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.incomeexpensetracker.dao.CategoryDao
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.CategoryEntity
import com.example.incomeexpensetracker.transactions.SubcategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val categoryDao: CategoryDao = AppDatabase.getDatabase(application).categoryDao()
    val categoriesFlow: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    private val subcategoryDao = AppDatabase.getDatabase(application).subcategoryDao()

    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryDao.insertCategory(category = category)
        }
    }

    // Fetch categories first to get the categoryId by categoryType (Income/Expense)
    fun getCategoryIdByType(type: String): Flow<Int> {
        return categoryDao.getCategoryIdByType(type)
    }

    // Fetch subcategories by category
    fun getSubcategoriesByCategory(categoryId: Int): Flow<List<SubcategoryEntity>> {
        return subcategoryDao.getSubcategoriesByCategory(categoryId)
    }

}
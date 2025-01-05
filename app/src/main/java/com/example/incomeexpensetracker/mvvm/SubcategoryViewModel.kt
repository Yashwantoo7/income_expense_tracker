package com.example.incomeexpensetracker.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.incomeexpensetracker.dao.SubcategoryDao
import com.example.incomeexpensetracker.db.AppDatabase
import com.example.incomeexpensetracker.transactions.SubcategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SubcategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val subcategoryDao: SubcategoryDao = AppDatabase.getDatabase(application).subcategoryDao()

    // Fetch all subcategories
    val allSubcategories: Flow<List<SubcategoryEntity>> = subcategoryDao.getAllSubcategories()

    // Save a new subcategory
    fun addSubcategory(subcategory: SubcategoryEntity) {
        viewModelScope.launch {
            //check if subcategory already exist or not
            if (subcategoryDao.getSubcategoryByName(subcategory.name) != null) {
                return@launch
            }
            subcategoryDao.insertSubcategory(subcategory)
        }
    }

    // Delete a subcategory
    fun deleteSubcategory(subcategory: SubcategoryEntity) {
        viewModelScope.launch {
            subcategoryDao.deleteSubcategory(subcategory)
        }
    }
}

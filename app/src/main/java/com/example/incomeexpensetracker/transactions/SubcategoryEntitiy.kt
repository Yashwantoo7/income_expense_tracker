package com.example.incomeexpensetracker.transactions

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "subcategories",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SubcategoryEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, //e.g., "Salary", "Gaming", "Food"
    val categoryId: Int // Foreign key referencing the CategoryEntity
)
package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val note: String,
    val dateString: String,
    val type: String, // "EXPENSE" or "INCOME"
    val timestamp: Long = System.currentTimeMillis(),
    val isFamilyShared: Boolean = false
)

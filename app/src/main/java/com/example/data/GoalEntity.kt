package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val targetMonthsTimeline: Int, // Number of months to achieve
    val category: String, // "House", "Car", "Startup", "Retirement", "Education", "Marriage"
    val timestamp: Long = System.currentTimeMillis()
)

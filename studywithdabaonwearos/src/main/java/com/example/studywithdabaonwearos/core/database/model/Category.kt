package com.example.dabastudy.core.database.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(

    val name: String,
    val timestamp: Long,
    val lastAdded: Long = timestamp,
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
)

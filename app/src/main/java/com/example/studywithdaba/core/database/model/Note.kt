package com.example.dabastudy.core.database.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(

    val title: String = "",
    val content: String = "",
    val contentStyle: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val favourite: Boolean = false,
    val lastEdited: Long = timestamp,
    @PrimaryKey(autoGenerate = true)
    val noteId: Long = 0,
)

package com.example.dabastudy.core.database.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Deck(

    val title: String,
    val description: String = "",
    val favourite: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis(),
    val lastOpened: Long = timeStamp,
    @PrimaryKey(autoGenerate = true)
    val deckId: Long = 0,
)

package com.example.dabastudy.core.database.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    val query: String,
    val searchScope: SearchScope = SearchScope.ALL,
    val lastTimeUsed: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val searchHistoryId: Long = 0,
)

enum class SearchScope {
    ALL,
    NOTE_ONLY,
    FLASHCARD_ONLY
}


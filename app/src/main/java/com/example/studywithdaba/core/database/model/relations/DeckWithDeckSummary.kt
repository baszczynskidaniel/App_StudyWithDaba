package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.dabastudy.core.database.model.entities.Deck

data class DeckWithDeckSummary(
    @Embedded val deck: Deck,
    @ColumnInfo(name = "countFlashcards") val numberOfFlashcards: Int,
    @ColumnInfo(name = "countNew") val numberOfNewFlashcards: Int,
    @ColumnInfo(name = "countToRepeat") val numberOfToRepeatFlashcards: Int,
    @ColumnInfo(name = "countRepeated") val numberOfRepeatedFlashcards: Int,
    @ColumnInfo(name = "countFavourite") val numberOfFavouriteFlashcards: Int,
) {
    fun summaryToString(): String {
        return "Total $numberOfFlashcards\nNew: $numberOfNewFlashcards\nTo Repeat: $numberOfToRepeatFlashcards\nRepeated: $numberOfRepeatedFlashcards\nFavourite: $numberOfFavouriteFlashcards"
    }
}

package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Deck

@Entity(
    primaryKeys = ["deckId", "flashcardId"],
    foreignKeys = [
        ForeignKey(
            entity = Flashcard::class,
            parentColumns = ["flashcardId"],
            childColumns = ["flashcardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["deckId"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class DeckFlashcardCrossRef(
    val deckId: Long,
    val flashcardId: Long,
)

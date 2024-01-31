package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.Deck

@Entity(
    primaryKeys = ["deckId", "noteId"],
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["noteId"],
            childColumns = ["noteId"],
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
data class DeckNoteCrossRef(
    val deckId: Long,
    val noteId: Long,
)

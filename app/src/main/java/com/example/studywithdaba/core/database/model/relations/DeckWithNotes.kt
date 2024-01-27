package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.Deck

data class DeckWithNotes(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "deckId",
        entityColumn = "noteId",
        associateBy = Junction(DeckNoteCrossRef::class)
    )
    val notes: List<Note>

)

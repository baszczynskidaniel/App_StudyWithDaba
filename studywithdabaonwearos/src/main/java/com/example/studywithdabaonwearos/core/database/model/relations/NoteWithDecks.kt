package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.Deck

data class NoteWithDecks(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "deckId",
        associateBy = Junction(DeckNoteCrossRef::class)
    )
    val decks: List<Deck>
)

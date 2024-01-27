package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Deck

data class DeckWithFlashcards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "deckId",
        entityColumn = "flashcardId",
        associateBy = Junction(DeckFlashcardCrossRef::class)
    )
    val flashcards: List<Flashcard>

)

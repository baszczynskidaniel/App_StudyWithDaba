package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Deck

data class FlashcardWithDeck(
    @Embedded val flashcard: Flashcard,
    @Relation(
        parentColumn = "flashcardId",
        entityColumn = "deckId",
        associateBy = Junction(DeckFlashcardCrossRef::class)
    )
    val decks: List<Deck>
)

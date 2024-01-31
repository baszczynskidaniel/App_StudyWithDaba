package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.FlashcardStatus
import com.example.dabastudy.core.database.model.entities.relations.CategoryFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {

    suspend fun insertFlashcard(flashcard: Flashcard): Long
    suspend fun removeFlashcardById(flashcardId: Long)
    fun getFlashcardsInDeck(deckId: Long): Flow<List<Flashcard>>
    fun getFlashcards(): Flow<List<Flashcard>>
    suspend fun getFlashcardById(flashcardId: Long): Flashcard?

    suspend fun insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs: List<DeckFlashcardCrossRef>)
    suspend fun updateFlashcard(flashcardId: Long, definition: String, answer: String)
    suspend fun updateFlashcardFavourite(flashcardId: Long, favourite: Boolean)
    suspend fun removeDeckFlashcardCrossRefsForFlashcardId(flashcardId: Long)
    suspend fun insertDeckFlashcardCrossRef(deckFlashcardCrossRef: DeckFlashcardCrossRef): Long
    suspend fun updateFlashcardRepetition(
        flashcardId: Long,
        nextRepetition: Long,
        state: FlashcardStatus,
        score: Int
    )
}
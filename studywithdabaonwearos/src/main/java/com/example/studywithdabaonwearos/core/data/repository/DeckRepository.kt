package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.relations.DeckWithDeckSummary
import kotlinx.coroutines.flow.Flow

interface DeckRepository {

    suspend fun insertDeck(deck: Deck): Long
    suspend fun removeDeckById(deckId: Long)
    suspend fun getDeckWithDeckSummaryById(deckId: Long): DeckWithDeckSummary
    fun getDeckWithDeckSummaries(): Flow<List<DeckWithDeckSummary>>
    suspend fun getDeckById(deckId: Long): Deck?
    fun getDecksInFlashcard(flashcardId: Long): Flow<List<Deck>>
    fun getDecks(): Flow<List<Deck>>
    suspend fun updateDeckFavourite(deckId: Long, favourite: Boolean)
}
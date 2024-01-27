package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.dao.DeckDao
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.relations.DeckWithDeckSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeckRepositoryImpl @Inject constructor(
    private val deckDao: DeckDao
): DeckRepository {

    override suspend fun insertDeck(deck: Deck): Long {
        return deckDao.insertDeck(deck)
    }
    override suspend fun removeDeckById(deckId: Long) {
        deckDao.removeDeckById(deckId)
    }

    override suspend fun getDeckWithDeckSummaryById(deckId: Long): DeckWithDeckSummary {
        return deckDao.getDeckSummaryByDeckId(deckId)
    }

    override fun getDeckWithDeckSummaries(): Flow<List<DeckWithDeckSummary>> {
        return deckDao.getDeckWithSummary()
    }

    override suspend fun getDeckById(deckId: Long): Deck? {
        return deckDao.getDeckById(deckId)
    }


}
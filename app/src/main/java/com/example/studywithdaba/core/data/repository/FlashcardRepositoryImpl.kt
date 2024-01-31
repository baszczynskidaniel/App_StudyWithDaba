package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.dao.FlashcardDao
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.FlashcardStatus
import com.example.dabastudy.core.database.model.entities.relations.CategoryFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao
): FlashcardRepository {
    override suspend fun insertFlashcard(flashcard: Flashcard): Long {
        return flashcardDao.insertFlashcard(flashcard)
    }

    override suspend fun removeFlashcardById(flashcardId: Long) {
        flashcardDao.removeFlashcardById(flashcardId)
    }

    override fun getFlashcardsInDeck(deckId: Long): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsInDeck(deckId)
    }

    override suspend fun insertDeckFlashcardCrossRef(deckFlashcardCrossRef: DeckFlashcardCrossRef): Long {
        return flashcardDao.insertDeckFlashcardCrossRef(deckFlashcardCrossRef)
    }

    override fun getFlashcards(): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcards()
    }

    override suspend fun getFlashcardById(flashcardId: Long): Flashcard? {
        return flashcardDao.getFlashcardById(flashcardId)
    }

    override suspend fun updateFlashcardRepetition(flashcardId: Long, nextRepetition: Long, state: FlashcardStatus, score: Int){
        flashcardDao.updateFlashcardRepetition(flashcardId, nextRepetition, state, score)
    }

    override suspend fun insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs: List<DeckFlashcardCrossRef>) {
        flashcardDao.insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs)
    }

    override suspend fun updateFlashcard(
        flashcardId: Long,
        definition: String,
        answer: String
    ) {
        flashcardDao.updateFlashcard(flashcardId, definition, answer)
    }

    override suspend fun updateFlashcardFavourite(
        flashcardId: Long,
        favourite: Boolean
    ) {
        flashcardDao.updateFlashcardFavourite(flashcardId, favourite)
    }

    override suspend fun removeDeckFlashcardCrossRefsForFlashcardId(flashcardId: Long) {
        flashcardDao.removeDeckFlashcardCrossRefsForFlashcardId(flashcardId)
    }
}

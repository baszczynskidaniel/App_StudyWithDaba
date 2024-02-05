package com.example.dabastudy

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.dabastudy.core.database.dao.DeckDao
import com.example.dabastudy.core.database.dao.FlashcardDao
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.studywithdaba.core.database.util.AppDatabase
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class FlashcardDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var flashcardDao: FlashcardDao
    private lateinit var deckDao: DeckDao

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        flashcardDao = database.flashcardDao
        deckDao = database.deckDao
    }

    @After
    fun closedDatabase() {
        database.close()
    }

    @Test
    fun getFlashcardsInDeck() = runTest {
        val flashcard1Id = flashcardDao.insertFlashcard(Flashcard(front = "a", back = "1"))
        val flashcard2Id = flashcardDao.insertFlashcard(Flashcard(front = "b", back = "2"))
        val flashcard3Id = flashcardDao.insertFlashcard(Flashcard(front = "c", back = "3"))
        val flashcard4Id = flashcardDao.insertFlashcard(Flashcard(front = "d", back = "4"))
        val flashcard5Id = flashcardDao.insertFlashcard(Flashcard(front = "e", back = "5"))
        val flashcard6Id = flashcardDao.insertFlashcard(Flashcard(front = "f", back = "6"))

        val deck1Id = deckDao.insertDeck(Deck("a"))
        val deck2Id = deckDao.insertDeck(Deck("b"))

        deckDao.insertStudySetFlashcardCrossRef(DeckFlashcardCrossRef(deck1Id, flashcard1Id))
        deckDao.insertStudySetFlashcardCrossRef(DeckFlashcardCrossRef(deck1Id, flashcard2Id))
        deckDao.insertStudySetFlashcardCrossRef(DeckFlashcardCrossRef(deck1Id, flashcard3Id))
        deckDao.insertStudySetFlashcardCrossRef(DeckFlashcardCrossRef(deck2Id, flashcard1Id))
        deckDao.insertStudySetFlashcardCrossRef(DeckFlashcardCrossRef(deck2Id, flashcard4Id))

        flashcardDao.getFlashcardsInDeck(deck1Id).test {
            val flashcards = awaitItem()
            println(flashcards)
            println("lol")
            assertThat(
                "Should get all flashcards in deck",
                flashcards.size,
                equalTo(3)
            )
        }
    }
}
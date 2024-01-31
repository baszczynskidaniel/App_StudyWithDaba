package com.example.dabastudy.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.FlashcardStatus
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckNoteCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckWithDeckSummary
import com.example.dabastudy.core.database.model.entities.relations.DeckWithFlashcards
import com.example.dabastudy.core.database.model.entities.relations.DeckWithNotes
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Query("update deck set favourite = :favourite where deckId = :deckId")
    suspend fun updateDeckFavourite(deckId: Long, favourite: Boolean)
    @Query("select * from deck")
    fun getDecks(): Flow<List<Deck>>

    @Query("select * from deck where deckId = :deckId")
    suspend fun getDeckById(deckId: Long): Deck?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck): Long

    @Delete
    suspend fun removeDeck(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudySetNoteCrossRef(crossRef: DeckNoteCrossRef): Long


    @Query("select * from DeckNoteCrossRef")
    fun getStudySetNoteCrossRef(): Flow<List<DeckNoteCrossRef>>

    @Delete()
    suspend fun deleteStudySetNoteCrossRef(crossRef: DeckNoteCrossRef)

    @Query("select * from deck where deckId = :studySetId")
    fun getNotesOfStudySet(studySetId: Long): Flow<List<DeckWithNotes>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudySetFlashcardCrossRef(crossRef: DeckFlashcardCrossRef): Long

    @Query("select * from deckflashcardcrossref")
    fun getStudySetFlashcardCrossRef(): Flow<List<DeckFlashcardCrossRef>>

    @Delete()
    suspend fun deleteStudySetFlashcardCrossRef(crossRef: DeckFlashcardCrossRef)

    @Query("select * from deck where deckId = :deckId")
    fun getFlashcardsOfDeck(deckId: Long): Flow<List<DeckWithFlashcards>>

    @Query("delete from deck where deckId=:deckId")
    suspend fun removeDeckById(deckId: Long)

    @Query("SELECT * FROM deck WHERE deckId IN (SELECT deckId FROM deckflashcardcrossref WHERE flashcardId = :flashcardId) GROUP BY deckId")
    fun getDecksInFlashcard(flashcardId: Long): Flow<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard): Long

    @Query("select count(*) from deckflashcardcrossref where deckId = :deckId")
    suspend fun getNumberOfFlashcardInDeck(deckId: Long): Int

    @Query("select count(*) from deckflashcardcrossref " +
            "inner join flashcard on deckflashcardcrossref.flashcardId = flashcard.flashcardId" +
            " where deckId = :deckId and state = :state")
    suspend fun getNumberOfFlashcardWithState(deckId: Long, state: FlashcardStatus): Int

    @Query(
        "select Deck.*, " +
                "count(case when state = \"NEW\" THEN deckflashcardcrossref.flashcardId END ) AS \"countNew\",  " +
                "count(case when state = \"TO_REPEAT\" THEN deckflashcardcrossref.flashcardId END) AS \"countToRepeat\", " +
                "count(case when state = \"REPEATED\" THEN deckflashcardcrossref.flashcardId END) AS \"countRepeated\", " +
                "count(flashcard.flashcardId) AS \"countFlashcards\", " +
                "count(case when flashcard.favourite = 1 THEN deckflashcardcrossref.flashcardId END ) AS \"countFavourite\" " +
                "from deck Left join deckflashcardcrossref on deckflashcardcrossref.deckId = deck.deckId" +
                " left  join flashcard on deckflashcardcrossref.flashcardId = flashcard.flashcardId group by deck.deckId"
    )
    fun getDeckWithSummary(): Flow<List<DeckWithDeckSummary>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs: List<DeckFlashcardCrossRef>)

    @Query(
        "select Deck.*, " +
                "count(case when state = \"NEW\" THEN deckflashcardcrossref.flashcardId END ) AS \"countNew\",  " +
                "count(case when state = \"TO_REPEAT\" THEN deckflashcardcrossref.flashcardId END) AS \"countToRepeat\", " +
                "count(case when state = \"REPEATED\" THEN deckflashcardcrossref.flashcardId END) AS \"countRepeated\", " +
                "count(flashcard.flashcardId) AS \"countFlashcards\", " +
                "count(case when flashcard.favourite = 1 THEN deckflashcardcrossref.flashcardId END ) AS \"countFavourite\" " +
                "from deck Left join deckflashcardcrossref on deck.deckId = deckflashcardcrossref.deckId" +
                " left  join flashcard on deckflashcardcrossref.flashcardId = flashcard.flashcardId where deck.deckId = :deckId group by deck.deckId limit 1"
    )
    suspend fun getDeckSummaryByDeckId(deckId: Long): DeckWithDeckSummary

}
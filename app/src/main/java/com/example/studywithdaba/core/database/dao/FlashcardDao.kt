package com.example.dabastudy.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.FlashcardStatus
import com.example.dabastudy.core.database.model.entities.relations.CategoryFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckWithDeckSummary
import com.example.dabastudy.core.database.model.entities.relations.FlashcardWithCategories
import com.example.dabastudy.core.database.model.entities.relations.NoteWithFlashcards
import kotlinx.coroutines.flow.Flow


@Dao
interface FlashcardDao {

    @Query("select * from flashcard")
    fun getFlashcards(): Flow<List<Flashcard>>

    @Query("select * from flashcard where flashcardId = :id")
    suspend fun getFlashcardById(id: Long): Flashcard?

    @Query("update flashcard set nextRepetition = :nextRepetition, level = :score, state = :state where flashcardId = :flashcardId")
    suspend fun updateFlashcardRepetition(flashcardId: Long, nextRepetition: Long, state: FlashcardStatus, score: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeckFlashcardCrossRef(deckFlashcardCrossRef: DeckFlashcardCrossRef): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard): Long

    @Delete
    suspend fun deleteFlashcard(flashcard: Flashcard)

    @Query("delete from flashcard where flashcardId = :flashcardId")
    suspend fun removeFlashcardById(flashcardId: Long)

    @Query("update flashcard set favourite = :isFavourite where flashcardId = :flashcardId")
    suspend fun updateFlashcardFavourite(flashcardId: Long, isFavourite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryFlashcardCrossRef(crossRef: CategoryFlashcardCrossRef): Long

    @Query("update flashcard set front = :definition, " +
            "back = :answer " +
            "where flashcardId = :flashcardId")
    suspend fun updateFlashcard(flashcardId: Long, definition: String, answer: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeckFlashcardCrossRefs(crossRefs: List<DeckFlashcardCrossRef>)

    @Query("update flashcard set state = \"TO_REPEAT\" where state = \"REPEATED\" and nextRepetition < :currentDate")
    suspend fun updateFlashcardStateWithDate(currentDate: Long)


    @Query("delete from DeckFlashcardCrossRef where flashcardId = :flashcardId")
    suspend fun removeDeckFlashcardCrossRefsForFlashcardId(flashcardId: Long)
    @Query("select * from categoryflashcardcrossref")
    fun getCategoryFlashcardCrossRef(): Flow<List<CategoryFlashcardCrossRef>>

    @Query("SELECT * FROM flashcard WHERE flashcardId IN (SELECT flashcardId FROM deckflashcardcrossref WHERE deckId = :deckId) GROUP BY flashcardId")
    fun getFlashcardsInDeck(deckId: Long): Flow<List<Flashcard>>



    @Query("select * from flashcard where state = :flashcardStatus")
    fun getFlashcardsWithStatus(flashcardStatus: FlashcardStatus): Flow<List<Flashcard>>

    @Query("select flashcard.* from flashcard " +
            "left outer join deckflashcardcrossref on deckflashcardcrossref.flashcardId = flashcard.flashcardId" +
            " where deckflashcardcrossref.deckId = :deckId and flashcard.state = :flashcardStatus")
    fun getFlashcardsInDeckWithStatus(deckId: Long, flashcardStatus: FlashcardStatus): Flow<List<Flashcard>>



    @Delete()
    suspend fun deleteCategoryFlashcardCrossRef(crossRef: CategoryFlashcardCrossRef)

    @Query("select * from flashcard where flashcardId = :flashcardId")
    fun getCategoriesOfFlashcard(flashcardId: Long): Flow<List<FlashcardWithCategories>>

    @Query("select * from note where noteId = :id")
    fun getNoteWithFlashcards(id: Long): Flow<List<NoteWithFlashcards>>





}
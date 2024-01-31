package com.example.studywithdaba.core.database.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dabastudy.core.database.dao.CategoryDao
import com.example.dabastudy.core.database.dao.DeckDao
import com.example.dabastudy.core.database.dao.FlashcardDao
import com.example.dabastudy.core.database.dao.NoteDao
import com.example.dabastudy.core.database.dao.SearchHistoryDao
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.SearchHistory
import com.example.dabastudy.core.database.model.entities.relations.CategoryFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.CategoryNoteCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.DeckNoteCrossRef

@Database(
    entities = [
        Note::class,
        Category::class,
        CategoryFlashcardCrossRef::class,
        Flashcard::class,
        CategoryNoteCrossRef::class,
        Deck::class,
        DeckFlashcardCrossRef::class,
        DeckNoteCrossRef::class,
        SearchHistory::class,

    ],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {
    abstract val categoryDao: CategoryDao
    abstract val noteDao: NoteDao
    abstract val deckDao: DeckDao
    abstract val flashcardDao: FlashcardDao
    abstract val searchHistoryDao: SearchHistoryDao
    companion object {
        const val DATABASE_NAME = "app_db"
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
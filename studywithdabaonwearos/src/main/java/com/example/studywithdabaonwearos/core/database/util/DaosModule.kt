package com.example.studywithdaba.core.database.util

import com.example.dabastudy.core.database.dao.CategoryDao
import com.example.dabastudy.core.database.dao.DeckDao
import com.example.dabastudy.core.database.dao.FlashcardDao
import com.example.dabastudy.core.database.dao.NoteDao
import com.example.dabastudy.core.database.dao.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {
    @Provides
    fun provideNoteDao(
        database: AppDatabase
    ): NoteDao = database.noteDao

    @Provides
    fun provideDeckDao(
        database: AppDatabase
    ): DeckDao = database.deckDao

    @Provides
    fun provideFlashcardDao(
        database: AppDatabase
    ): FlashcardDao = database.flashcardDao

    @Provides
    fun provideCategoryDao(
        database: AppDatabase
    ): CategoryDao = database.categoryDao

    @Provides
    fun provideSearchHistoryDao(
        database: AppDatabase
    ): SearchHistoryDao = database.searchHistoryDao
}
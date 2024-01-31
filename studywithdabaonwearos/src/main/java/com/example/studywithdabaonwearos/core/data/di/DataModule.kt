package com.example.studywithdaba.core.data.di

import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.DeckRepositoryImpl
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepositoryImpl
import com.example.studywithdaba.core.data.repository.NoteRepository
import com.example.studywithdaba.core.data.repository.NoteRepositoryImpl
import com.example.studywithdaba.core.data.repository.SearchHistoryRepository
import com.example.studywithdaba.core.data.repository.SearchHistoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindDeckRepository(
        deckRepository: DeckRepositoryImpl,
    ): DeckRepository
    @Binds
    fun bindFlashcardRepository(
        flashcardRepository: FlashcardRepositoryImpl,
    ): FlashcardRepository


    @Binds
    fun bindSearchHistoryRepository(
        searchHistoryRepository: SearchHistoryRepositoryImpl,
    ): SearchHistoryRepository

    @Binds
    fun bindNoteRepository(
        noteRepository: NoteRepositoryImpl,
    ): NoteRepository



}
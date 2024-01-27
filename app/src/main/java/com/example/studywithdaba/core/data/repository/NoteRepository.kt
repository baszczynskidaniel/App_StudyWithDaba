package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.model.entities.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun insertNote(note: Note): Long
    suspend fun removeNoteById(noteId: Long)
    suspend fun getNoteById(noteId: Long): Note?
    fun getNotes(): Flow<List<Note>>
    suspend fun updateFavouriteForNoteIds(noteIds: List<Long>, favourite: Boolean)
    suspend fun removeNoteByNoteIds(noteIds: List<Long>)
    suspend fun updateFavouriteByNoteId(noteId: Long, favourite: Boolean)
    suspend fun updateNoteTitleByNoteId(noteId: Long, title: String, lastEdited: Long = System.currentTimeMillis())
    suspend fun updateNoteContentByNoteId(noteId: Long, content: String, lastEdited: Long = System.currentTimeMillis())

}
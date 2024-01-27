package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.dao.NoteDao
import com.example.dabastudy.core.database.model.entities.Note
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
): NoteRepository {
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note)
    }

    override suspend fun removeNoteById(noteId: Long) {
        noteDao.removeNoteById(noteId)
    }

    override suspend fun getNoteById(noteId: Long): Note? {
        return noteDao.getNoteById(noteId)
    }

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes()
    }

    override suspend fun updateFavouriteForNoteIds(noteIds: List<Long>, favourite: Boolean) {
        noteDao.updateFavouriteForNoteIds(noteIds, favourite)
    }

    override suspend fun removeNoteByNoteIds(noteIds: List<Long>) {
        noteDao.removeNoteByNoteIds(noteIds)
    }

    override suspend fun updateFavouriteByNoteId(noteId: Long, favourite: Boolean) {
        noteDao.updateFavouriteByNoteId(noteId, favourite)
    }

    override suspend fun updateNoteTitleByNoteId(noteId: Long, title: String, lastEdited: Long) {
        noteDao.updateNoteTitleByNoteId(noteId, title, lastEdited)
    }

    override suspend fun updateNoteContentByNoteId(
        noteId: Long,
        content: String,
        lastEdited: Long
    ) {
        noteDao.updateNoteContentByNoteId(noteId, content, lastEdited)
    }
}
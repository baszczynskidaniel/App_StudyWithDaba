package com.example.dabastudy.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.relations.CategoryNoteCrossRef
import com.example.dabastudy.core.database.model.entities.relations.NoteWithCategories
import com.example.dabastudy.core.database.model.entities.relations.NoteWithFlashcards
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("update note set title = :title, lastEdited = :lastEdited where noteId = :noteId")
    suspend fun updateNoteTitleByNoteId(noteId: Long, title: String, lastEdited: Long )

    @Query("update note set content = :content, lastEdited = :lastEdited where noteId = :noteId")
    suspend fun updateNoteContentByNoteId(noteId: Long, content: String, lastEdited: Long )


    @Query("select * from note")
    fun getNotes(): Flow<List<Note>>

    @Query("select * from note where noteId = :id")
    suspend fun getNoteById(id: Long): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Query("select * from categorynotecrossref")
    fun getCategoryNoteCrossRef(): Flow<List<CategoryNoteCrossRef>>

    @Query("select * from note where noteId = :id")
    fun getNoteWithFlashcards(id: Long): Flow<List<NoteWithFlashcards>>

    @Query("update note set favourite = :favourite where noteId = :noteId")
    fun updateFavouriteByNoteId(noteId: Long, favourite: Boolean)

    @Delete
    suspend fun deleteNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryNoteCrossRef(crossRef: CategoryNoteCrossRef): Long

    @Delete()
    suspend fun deleteCategoryNoteCrossRef(crossRef: CategoryNoteCrossRef)

    @Query("select * from note where noteId = :noteId")
    fun getCategoryOfNotes(noteId: Long): Flow<List<NoteWithCategories>>

    @Query("delete from note where noteId=:noteId")
    suspend fun removeNoteById(noteId: Long)

    @Query("update note set favourite = :favourite where noteId In (:noteIds)")
    fun updateFavouriteForNoteIds(noteIds: List<Long>, favourite: Boolean)

    @Query("delete from note where noteId In (:noteIds)")
    suspend fun removeNoteByNoteIds(noteIds: List<Long>)
}
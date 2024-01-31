package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Note


data class NoteWithFlashcards(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "noteOriginId"
    )
    val flashcards: List<Flashcard>
)

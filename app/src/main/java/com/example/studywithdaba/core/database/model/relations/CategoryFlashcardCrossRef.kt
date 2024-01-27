package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Note

@Entity(
    primaryKeys = ["categoryId", "flashcardId"],
    foreignKeys = [
        ForeignKey(
            entity = Flashcard::class,
            parentColumns = ["flashcardId"],
            childColumns = ["flashcardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class CategoryFlashcardCrossRef(
    val categoryId: Long,
    val flashcardId: Long,
)

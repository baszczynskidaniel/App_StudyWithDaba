package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.Flashcard

data class CategoryWithFlashcards(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "flashcardId",
        associateBy = Junction(CategoryFlashcardCrossRef::class)
    )
    val flashcards: List<Flashcard>

)

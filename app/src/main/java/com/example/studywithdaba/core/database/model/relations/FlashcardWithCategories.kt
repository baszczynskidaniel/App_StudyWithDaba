package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.Flashcard

data class FlashcardWithCategories(
    @Embedded val flashcard: Flashcard,
    @Relation(
        parentColumn = "flashcardId",
        entityColumn = "categoryId",
        associateBy = Junction(CategoryFlashcardCrossRef::class)
    )
    val categories: List<Category>

)

package com.example.dabastudy.core.database.model.entities.relations

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.Note

@Entity(
    primaryKeys = ["categoryId", "noteId"],
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["noteId"],
            childColumns = ["noteId"],
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
data class CategoryNoteCrossRef(
    val categoryId: Long,
    val noteId: Long,
)

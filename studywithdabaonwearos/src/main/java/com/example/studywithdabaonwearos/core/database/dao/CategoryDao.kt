package com.example.dabastudy.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.relations.CategoryFlashcardCrossRef
import com.example.dabastudy.core.database.model.entities.relations.CategoryNoteCrossRef
import com.example.dabastudy.core.database.model.entities.relations.CategoryWithFlashcards
import com.example.dabastudy.core.database.model.entities.relations.CategoryWithNotes
import com.example.dabastudy.core.database.model.entities.relations.NoteWithCategories
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {

    @Query("select * from category")
    fun getCategories(): Flow<List<Category>>

    @Query("select * from category where categoryId = :id")
    suspend fun getCategoryById(id: Long): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Delete
    suspend fun deleteCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryNoteCrossRef(crossRef: CategoryNoteCrossRef): Long


    @Query("select * from categorynotecrossref")
    fun getCategoryNoteCrossRef(): Flow<List<CategoryNoteCrossRef>>

    @Delete()
    suspend fun deleteCategoryNoteCrossRef(crossRef: CategoryNoteCrossRef)

    @Query("select * from category where categoryId = :categoryId")
    fun getNotesOfCategory(categoryId: Long): Flow<List<CategoryWithNotes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryFlashcardCrossRef(crossRef: CategoryFlashcardCrossRef): Long

    @Delete()
    suspend fun deleteCategoryFlashcardsCrossRef(crossRef: CategoryFlashcardCrossRef)

    @Query("select * from category where categoryId = :categoryId")
    fun getFlashcardsOfCategory(categoryId: Long): Flow<List<CategoryWithFlashcards>>

    @Query("select * from categoryflashcardcrossref")
    fun getCategoryFlashcardCrossRef(): Flow<List<CategoryFlashcardCrossRef>>


}
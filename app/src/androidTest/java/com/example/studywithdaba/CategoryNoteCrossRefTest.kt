package com.example.studywithdaba

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.dabastudy.core.database.dao.CategoryDao
import com.example.dabastudy.core.database.dao.NoteDao
import com.example.dabastudy.core.database.model.entities.Category
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.relations.CategoryNoteCrossRef
import com.example.studywithdaba.core.database.util.AppDatabase
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CategoryNoteCrossRefTest {
    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao
    private lateinit var noteDao: NoteDao
    private val testableCategory = Category("test", 10)
    private val testableNote = Note("title", "content", "", 20,)

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        categoryDao = database.categoryDao
        noteDao = database.noteDao
    }

    @After
    fun closedDatabase() {
        database.close()
    }

    @Test
    fun insertCategoryNoteCrossRefTestEntitySize() = runTest {
        val noteId = noteDao.insertNote(testableNote)
        val categoryId = categoryDao.insertCategory(testableCategory)
        categoryDao.insertCategoryNoteCrossRef(CategoryNoteCrossRef(categoryId, noteId))
        categoryDao.getCategoryNoteCrossRef().test {
            val crossRefs = awaitItem()

            assertThat(
                crossRefs.size,
                equalTo(1)
            )
        }
    }

    @Test
    fun deleteCategoryNoteCrossRefShouldDeleteCategoryRef() = runTest {
        val noteId = noteDao.insertNote(testableNote)
        val categoryId = categoryDao.insertCategory(testableCategory)
        val crossRefId = categoryDao.insertCategoryNoteCrossRef(CategoryNoteCrossRef(categoryId, noteId))

        categoryDao.deleteCategory(testableCategory.copy(categoryId = categoryId))

        categoryDao.getCategories().test {
            val categories = awaitItem()
            println(categories)
            assertThat(
                "category table should be empty",
                categories.size,
                equalTo(0)
            )
        }
        categoryDao.getCategoryNoteCrossRef().test {

            val crossRefs = awaitItem()
            println(crossRefs)
            assertThat(
                "categorynotecrossref table should be empty",
                crossRefs.size,
                equalTo(0)
            )
        }
    }

    @Test
    fun insertCategoryNoteRefCheckIfThereAreNoDuplicates() = runTest {
        val noteId = noteDao.insertNote(testableNote)
        val categoryId = categoryDao.insertCategory(testableCategory)
        categoryDao.insertCategoryNoteCrossRef(CategoryNoteCrossRef(categoryId, noteId))
        categoryDao.insertCategoryNoteCrossRef(CategoryNoteCrossRef(categoryId, noteId))
        categoryDao.insertCategoryNoteCrossRef(CategoryNoteCrossRef(categoryId, noteId))
        categoryDao.getCategoryNoteCrossRef().test {
            val crossRefs = awaitItem()
            assertThat(
                "When adding the same cross ref there should be no duplicates",
                crossRefs.size,
                equalTo(1)
            )
        }
    }

}
package com.example.studywithdaba

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.dabastudy.core.database.dao.CategoryDao
import com.example.dabastudy.core.database.model.entities.Category
import com.example.studywithdaba.core.database.util.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
): TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@RunWith(AndroidJUnit4::class)
@SmallTest
class CategoryDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var categoryDao: CategoryDao
    private val testableCategory = Category("test", 10)

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        categoryDao = database.categoryDao
    }

    @After
    fun closedDatabase() {
        database.close()
    }

    @Test
    fun insertCategory_returnsTrue() = runTest {
        val testableCategoryId = categoryDao.insertCategory(testableCategory)
        categoryDao.getCategories().test {
            val categories = awaitItem()
            assertThat(
                "test category size",
                categories.size,
                equalTo(1))
            assertThat(
                "test if category added to database is the same as category save in database, except id",
                testableCategory.copy(categoryId = testableCategoryId),
                equalTo(categories.first()))
            cancel()
        }
    }

    @Test
    fun deleteCategory() = runTest {
        val someCategory1 = Category("art", 20)
        val someCategory2 = Category("music", 30)

        categoryDao.insertCategory(someCategory1)
        val testableCategoryId = categoryDao.insertCategory(testableCategory)
        val testableCategoryFromDatabase = testableCategory.copy(categoryId = testableCategoryId)
        val someCategory2IdFromDatabase = categoryDao.insertCategory(someCategory2)

        categoryDao.deleteCategory(testableCategoryFromDatabase)
        categoryDao.getCategories().test {
            val categories = awaitItem()
            assertThat(
                "test correct size",
                categories.size,
                equalTo(2))
            assertThat(
                "test if deleted category is not in database",
                categories.contains(testableCategoryFromDatabase),
                equalTo(false))
            assertThat(
                "test if other category in database is unchanged ",
                categories.contains(someCategory2.copy(categoryId = someCategory2IdFromDatabase)),
                equalTo(true))
            cancel()
        }
    }

    @Test
    fun getCategoryById() = runTest {
        val testableCategoryIdFromDatabase = categoryDao.insertCategory(testableCategory)
        assertThat(
            "test if added category match it's version from database",
            categoryDao.getCategoryById(testableCategoryIdFromDatabase) == testableCategory.copy(categoryId = testableCategoryIdFromDatabase),
            equalTo(true)
        )
    }

    @Test
    fun updateCategory() = runTest {
        val categoryBeforeUpdate = Category("sport", 10)
        val someCategory = Category("art", 10)
        val idFromDatabase = categoryDao.insertCategory(categoryBeforeUpdate)
        categoryDao.insertCategory(someCategory)

        val updatedCategory = categoryBeforeUpdate.copy(categoryId = idFromDatabase, name = "music" )
        categoryDao.insertCategory(updatedCategory)
        categoryDao.getCategories().test {
            val categories = awaitItem()
            assertThat(
                "test if category update do not change category size",
                categories.size,
                equalTo(2)
            )
            assertThat(
                "test if updated category is in database",
                categories.contains(updatedCategory),
                equalTo(true)
            )
            assertThat(
                "test if category before update is not present in database",
                categories.contains(categoryBeforeUpdate),
                equalTo(false)
            )
            cancel()
        }
    }
}
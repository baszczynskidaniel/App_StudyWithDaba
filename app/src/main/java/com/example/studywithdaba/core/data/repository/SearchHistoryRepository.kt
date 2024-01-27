package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.SearchHistory
import com.example.dabastudy.core.database.model.entities.SearchScope
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun updateSearchHistoryById(searchHistoryId: Long, timestamp: Long = System.currentTimeMillis())
    fun getSearchHistoryInScope(searchScope: SearchScope): Flow<List<SearchHistory>>
    suspend fun removeOldestSearchHistory()
    suspend fun insertSearchHistory(searchHistory: SearchHistory): Long
    suspend fun removeSearchHistoryInScope(searchScope: SearchScope)

    suspend fun removeSearchHistoryById(searchHistoryId: Long)
}
package com.example.studywithdaba.core.data.repository

import com.example.dabastudy.core.database.dao.SearchHistoryDao
import com.example.dabastudy.core.database.model.entities.SearchHistory
import com.example.dabastudy.core.database.model.entities.SearchScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

const val MAX_SEARCH_HISTORY_IN_SCOPE = 16

class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
): SearchHistoryRepository{
    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchHistoryDao.getSearchHistory()
    }

    override suspend fun updateSearchHistoryById(searchHistoryId: Long, timestamp: Long) {
        searchHistoryDao.updateSearchHistoryById(searchHistoryId, System.currentTimeMillis())
    }

    override fun getSearchHistoryInScope(searchScope: SearchScope): Flow<List<SearchHistory>> {
        return searchHistoryDao.getSearchHistoryInScope(searchScope)
    }

    override suspend fun removeOldestSearchHistory() {
        searchHistoryDao.removeOldestSearchHistory()
    }

    override suspend fun insertSearchHistory(searchHistory: SearchHistory): Long {
        if(searchHistoryDao.getNumberOfSearchHistoryInScope(searchHistory.searchScope) > MAX_SEARCH_HISTORY_IN_SCOPE + 1)
            searchHistoryDao.removeOldestSearchHistory()
        return searchHistoryDao.insertSearchHistory(searchHistory)
    }

    override suspend fun removeSearchHistoryInScope(searchScope: SearchScope) {
        searchHistoryDao.removeSearchHistoryInScope(searchScope)
    }

    override suspend fun removeSearchHistoryById(searchHistoryId: Long) {
        searchHistoryDao.removeSearchHistoryById(searchHistoryId)
    }

}
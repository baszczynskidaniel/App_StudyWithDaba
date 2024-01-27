package com.example.dabastudy.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dabastudy.core.database.model.entities.SearchHistory
import com.example.dabastudy.core.database.model.entities.SearchScope
import kotlinx.coroutines.flow.Flow


@Dao
interface SearchHistoryDao {


    @Query("select * from searchhistory " +
            "order by lastTimeUsed DESC")
    fun getSearchHistory(): Flow<List<SearchHistory>>

    @Query("select count(*) from searchhistory " +
            "where searchScope = :searchScope")
    suspend fun getNumberOfSearchHistoryInScope(searchScope: SearchScope): Int

    @Query("update searchhistory " +
            "set lastTimeUsed = :timestamp " +
            "where searchHistoryId = :searchHistoryId")
    suspend fun updateSearchHistoryById(searchHistoryId: Long, timestamp: Long = System.currentTimeMillis())

    @Query("select * from searchhistory " +
            "where searchScope = :searchScope " +
            "order by lastTimeUsed DESC")
    fun getSearchHistoryInScope(searchScope: SearchScope): Flow<List<SearchHistory>>

    @Query("delete from searchhistory " +
            "where lastTimeUsed = (select min(lastTimeUsed) from searchhistory)")
    suspend fun removeOldestSearchHistory()

    @Query("delete from searchhistory " +
            "where searchHistoryId = :searchHistoryId")
    suspend fun removeSearchHistoryById(searchHistoryId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistory: SearchHistory): Long

    @Query("delete from searchHistory where searchScope = :searchScope")
    suspend fun removeSearchHistoryInScope(searchScope: SearchScope)


}
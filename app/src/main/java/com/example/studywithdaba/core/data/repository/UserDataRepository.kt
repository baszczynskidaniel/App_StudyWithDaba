package com.example.studywithdaba.core.data.repository

import com.example.studywithdaba.core.datastore.model.AppTheme
import com.example.studywithdaba.core.datastore.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {
    val userData: Flow<UserData>

    suspend fun setAppTheme(appTheme: AppTheme)
    suspend fun setUseDynamicColor(useDynamicColor: Boolean)
    suspend fun setNotesGridSize(gridSize: Int)
}
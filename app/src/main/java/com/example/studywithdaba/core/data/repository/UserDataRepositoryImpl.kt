package com.example.studywithdaba.core.data.repository

import com.example.studywithdaba.core.datastore.SWDPreferencesDataSource
import com.example.studywithdaba.core.datastore.model.AppTheme
import com.example.studywithdaba.core.datastore.model.UserData

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: SWDPreferencesDataSource,
): UserDataRepository {
    override val userData: Flow<UserData> = preferencesDataSource.userData

    override suspend fun setNotesGridSize(gridSize: Int) {
        preferencesDataSource.setNotesGridSize(gridSize)
    }

    override suspend fun setAppTheme(appTheme: AppTheme) {
        preferencesDataSource.setAppTheme(appTheme)
    }

    override suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        preferencesDataSource.setDynamicColor(useDynamicColor)
    }
}
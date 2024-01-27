package com.example.studywithdaba.core.datastore

import androidx.datastore.core.DataStore
import com.example.studywithdaba.core.datastore.model.AppTheme
import com.example.studywithdaba.core.datastore.model.UserData
import com.example.studywithdaba.core.datastore.proto.AppThemeProto
import com.example.studywithdaba.core.datastore.proto.UserPreferences
import com.example.studywithdaba.core.datastore.proto.copy
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SWDPreferencesDataSource
@Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {

    val userData = userPreferences.data
        .map {
            UserData(
                dynamicColor = it.useDynamicColor,
                theme = when(it.appThemeProto) {
                    AppThemeProto.APP_THEME_LIGHT -> AppTheme.Light
                    AppThemeProto.APP_THEME_Dark -> AppTheme.Dark
                    AppThemeProto.APP_THEME_Default -> AppTheme.Default
                    else -> AppTheme.Default
                },
                notesGridSize = if(it.notesGridSize == 0) 2 else it.notesGridSize
            )
        }

    suspend fun setNotesGridSize(gridSize: Int) {
        userPreferences.updateData { it.copy {
            this.notesGridSize = gridSize
        } }
    }

    suspend fun setAppTheme(appTheme: AppTheme){//edit your preferences with different methods
        userPreferences.updateData {
            it.copy {
                this.appThemeProto = when(appTheme) {
                    AppTheme.Light -> AppThemeProto.APP_THEME_LIGHT
                    AppTheme.Dark -> AppThemeProto.APP_THEME_Dark
                    else -> AppThemeProto.APP_THEME_Default
                }
            }
        }
    }
    suspend fun setDynamicColor(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.useDynamicColor = useDynamicColor
            }
        }
    }
}
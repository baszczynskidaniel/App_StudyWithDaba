package com.example.studywithdaba.core.datastore.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val theme: AppTheme = AppTheme.Default,
    val dynamicColor: Boolean = false,
    val notesGridSize: Int = 2,
)

enum class AppTheme {
    Light, Dark, Default;

    override fun toString(): String {
        return when(this) {
            Light -> "Light theme"
            Dark -> "Dark theme"
            Default -> "System theme"
        }
    }

    fun getIconForTheme(appTheme: AppTheme): ImageVector {
        return when(this) {
            Light -> SWDIcons.LightMode
            Dark -> SWDIcons.DarkMode
            Default -> SWDIcons.Device
        }
    }
}


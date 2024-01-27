package com.example.studywithdaba.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.studywithdaba.MainActivityState
import com.example.studywithdaba.core.data.repository.UserDataRepository
import com.example.studywithdaba.core.datastore.model.AppTheme
import com.example.studywithdaba.core.datastore.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
    private val _userData = userDataRepository.userData
        .map { userData ->
            SettingsState(
                isLoading = false,
                userData = userData
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsState()
        )

    private val _state = MutableStateFlow(SettingsState())
    val state = combine(_state, _userData) {
            state, userData -> state.copy(
        isLoading = false,
        userData = userData.userData
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.OnAppThemeChange -> {
                viewModelScope.launch {
                    userDataRepository.setAppTheme(event.appTheme)
                }
            }
            is SettingsEvent.OnUseDynamicColorChange -> {
                viewModelScope.launch {
                    userDataRepository.setUseDynamicColor(!event.value)
                }
            }

            is SettingsEvent.OnBack -> {
                event.navController.navigateUp()
            }

            SettingsEvent.OnAppThemeDialogDismiss -> {
                _state.update {
                    it.copy(
                        showThemeDialog = false
                    )
                }
            }
            SettingsEvent.OnAppearanceClick -> {
                _state.update {
                    it.copy(
                        showThemeDialog = true
                    )
                }
            }
        }
    }
}

data class SettingsState(
    val userData: UserData = UserData(),
    val isLoading: Boolean = true,
    val showThemeDialog: Boolean = false,
)


sealed class SettingsEvent {
    data class OnUseDynamicColorChange(val value: Boolean): SettingsEvent()
    data class OnAppThemeChange(val appTheme: AppTheme): SettingsEvent()
    data class OnBack(val navController: NavController): SettingsEvent()
    object OnAppearanceClick: SettingsEvent()
    object OnAppThemeDialogDismiss: SettingsEvent()
}
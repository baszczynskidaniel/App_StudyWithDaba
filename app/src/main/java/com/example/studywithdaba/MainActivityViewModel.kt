package com.example.studywithdaba

import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.UserDataRepository
import com.example.studywithdaba.core.datastore.model.UserData
import com.example.studywithdaba.core.design_system.component.AddFeaturesBottomSheetEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.concurrent.Flow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    private val _userData = userDataRepository.userData.map {
        MainActivityState(
            isLoading = false,
            userData = it
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityState(),
        started = SharingStarted.WhileSubscribed(5000)
    )
    private val _state = MutableStateFlow(MainActivityState())
    val state = combine(_state, _userData) {
        state, userData -> state.copy(
            isLoading = false,
            userData = userData.userData
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainActivityState())

    fun onEvent(event: MainActivityEvent) {
        when(event) {
            is MainActivityEvent.OnCurrentRouteChange -> {
                event.navController.navigate(event.route)
            }

            MainActivityEvent.OnAdd -> {
                _state.update { it.copy(
                    showBottomSheet = true
                ) }
            }

            is MainActivityEvent.OnBottomSheetEvent -> {
                when(event.event) {
                    AddFeaturesBottomSheetEvent.OnAddDeck -> {
                        _state.update { it.copy(
                            showBottomSheet = false
                        ) }
                        event.navController.navigate(Screen.AddDeck.route)
                    }
                    AddFeaturesBottomSheetEvent.OnAddFlashcards -> {
                        _state.update { it.copy(
                            showBottomSheet = false
                        ) }
                        event.navController.navigate(Screen.AddFlashcard.route)
                    }
                    AddFeaturesBottomSheetEvent.OnAddNote -> {
                        _state.update { it.copy(
                            showBottomSheet = false
                        ) }
                        event.navController.navigate(Screen.EditNote.route)
                    }
                    AddFeaturesBottomSheetEvent.OnDismiss -> _state.update { it.copy(
                        showBottomSheet = false
                    ) }
                }

            }

            MainActivityEvent.OnDismissAddDeckDialog -> _state.update { it.copy(
                showAddDeckDialog = false
            ) }
            MainActivityEvent.OnDismissAddFlashcardDialog -> _state.update { it.copy(
                showAddFlashcardDialog = false
            ) }
        }
    }
}

data class MainActivityState (
    val isLoading: Boolean = true,
    val userData: UserData = UserData(),
    val showNavigationBar: Boolean = true,
    val showBottomSheet: Boolean = false,
    val showAddFlashcardDialog: Boolean = false,
    val showAddDeckDialog: Boolean = false,
)

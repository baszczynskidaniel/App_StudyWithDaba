package com.example.studywithdaba.feature_deck.edit_deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){
    private val deckId = savedStateHandle.get<Long>("deckId") ?: -1
    private val _state = MutableStateFlow(EditDeckState())
    val state: StateFlow<EditDeckState> = _state

    init {
        viewModelScope.launch {
            val deck = deckRepository.getDeckById(deckId)!!
            _state.update { it.copy(
                title = deck.title,
                description = deck.description,
                favourite = deck.favourite

            ) }
        }
    }

    fun onEvent(event: EditDeckEvent) {
        when(event) {
            is EditDeckEvent.OnApplyChanges -> {
                if(_state.value.title.isBlank()) {
                    _state.update { it.copy(
                        titleError = "Title cannot be blank"
                    ) }
                } else {
                    viewModelScope.launch {

                        deckRepository.updateDeck(deckId, _state.value.title, _state.value.description)
                        deckRepository.updateDeckFavourite(deckId, _state.value.favourite)
                    }
                    event.navController.navigateUp()
                }
            }
            is EditDeckEvent.OnBack -> {
                event.navController.navigateUp()
            }
            is EditDeckEvent.OnDescriptionChange -> _state.update { it.copy(
                description = event.descriptionChange
            ) }
            EditDeckEvent.OnDescriptionClear -> _state.update { it.copy(
                description = ""
            ) }
            is EditDeckEvent.OnFavouriteChange -> _state.update { it.copy(
                favourite = event.favouriteChange
            ) }
            is EditDeckEvent.OnTitleChange -> _state.update { it.copy(
                title = event.titleChange,
                titleError = null
            ) }
            EditDeckEvent.OnTitleClear -> _state.update { it.copy(
                title = "",
                titleError = null
            ) }
        }
    }
}
data class EditDeckState(
    val title: String = "",
    val description: String = "",
    val favourite: Boolean = false,
    val titleError: String? = null,
)

sealed class EditDeckEvent {
    data class OnBack(val navController: NavController): EditDeckEvent()
    data class OnFavouriteChange(val favouriteChange: Boolean): EditDeckEvent()
    data class OnTitleChange(val titleChange: String): EditDeckEvent()
    object OnTitleClear: EditDeckEvent()
    data class OnDescriptionChange(val descriptionChange: String): EditDeckEvent()
    object OnDescriptionClear: EditDeckEvent()
    data class OnApplyChanges(val navController: NavController): EditDeckEvent()
}
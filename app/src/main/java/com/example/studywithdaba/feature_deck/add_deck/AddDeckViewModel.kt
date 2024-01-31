package com.example.studywithdaba.feature_deck.add_deck

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
class AddDeckViewModel @Inject constructor(
    private val deckRepository: DeckRepository
): ViewModel(){
    private val _state = MutableStateFlow(AddDeckState())
    val state: StateFlow<AddDeckState> = _state
    fun onEvent(event: AddDeckEvent) {
        when(event) {
            is AddDeckEvent.OnAddDeck -> {
                if(_state.value.title.isBlank()) {
                    _state.update { it.copy(
                        titleError = "Title cannot be blank"
                    ) }
                } else {
                    viewModelScope.launch {
                        val deckId = deckRepository.insertDeck(Deck(
                            title =_state.value.title,
                            description = _state.value.description,
                            favourite = _state.value.favourite
                        ) )
                        event.navController.navigate(Screen.FlashcardsInDeck.route + "?deckId=$deckId")
                    }
                }
            }
            is AddDeckEvent.OnBack -> {
                event.navController.navigateUp()
            }
            is AddDeckEvent.OnDescriptionChange -> _state.update { it.copy(
                description = event.descriptionChange
            ) }
            AddDeckEvent.OnDescriptionClear -> _state.update { it.copy(
                description = ""
            ) }
            is AddDeckEvent.OnFavouriteChange -> _state.update { it.copy(
                favourite = event.favouriteChange
            ) }
            is AddDeckEvent.OnTitleChange -> _state.update { it.copy(
                title = event.titleChange,
                titleError = null
            ) }
            AddDeckEvent.OnTitleClear -> _state.update { it.copy(
                title = "",
                titleError = null
            ) }
        }
    }
}
data class AddDeckState(
    val title: String = "",
    val description: String = "",
    val favourite: Boolean = false,
    val titleError: String? = null,
)

sealed class AddDeckEvent {
    data class OnBack(val navController: NavController): AddDeckEvent()
    data class OnFavouriteChange(val favouriteChange: Boolean): AddDeckEvent()
    data class OnTitleChange(val titleChange: String): AddDeckEvent()
    object OnTitleClear: AddDeckEvent()
    data class OnDescriptionChange(val descriptionChange: String): AddDeckEvent()
    object OnDescriptionClear: AddDeckEvent()
    data class OnAddDeck(val navController: NavController): AddDeckEvent()
}
package com.example.studywithdaba.feature_flashcard.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.relations.DeckWithDeckSummary
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DecksViewModel @Inject constructor(
    private val deckRepository: DeckRepository
): ViewModel() {


    private val _deckSummaries = deckRepository.getDeckWithDeckSummaries().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList()
    )

    private val _state = MutableStateFlow(DecksState())
    val state = combine(_state, _deckSummaries) {
        state, deckSummaries -> state.copy(
            decks = deckSummaries
        )
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), DecksState()
    )

    fun onEvent(event: DecksEvent) {
        when(event) {
            is DecksEvent.OnAddDeck -> {
                event.navController.navigate(Screen.AddDeck.route)
            }
            is DecksEvent.OnDeckClick -> {
                event.navController.navigate(Screen.FlashcardsInDeck.route + "?deckId=${event.deckId}")
            }
            is DecksEvent.OnDeckFavouriteClick -> {
                viewModelScope.launch { deckRepository.updateDeckFavourite(event.deckId, event.favouriteChange) }
            }
            is DecksEvent.OnDeckLongClick -> {

            }

            is DecksEvent.OnDeckMoreClick -> {
                _state.update { it.copy(
                    showDeckBottomSheet = true,
                    selectedDeckId = event.deckId
                )
                }
            }

            is DecksEvent.OnSettings -> {
                event.navController.navigate(Screen.Settings.route)
            }

            is DecksEvent.OnDeckBottomSheetEvent -> {
                when(event.event) {
                    DeckBottomSheetEvent.OnDismiss -> _state.update { it.copy(
                        showDeckBottomSheet = false
                        )
                    }

                    is DeckBottomSheetEvent.OnEditDeck -> {
                        _state.update { it.copy(
                            showDeckBottomSheet = false
                        )
                        }
                        event.event.navController.navigate(Screen.EditDeck.route + "?deckId=${event.event.deckId}")
                    }

                    is DeckBottomSheetEvent.OnRemoveDeck -> {
                        _state.update { it.copy(
                            showDeckBottomSheet = false
                        )
                        }
                        viewModelScope.launch {
                            deckRepository.removeDeckById(event.event.deckId)
                        }
                    }
                }
            }
        }
    }

}

data class DecksState(
    val decks: List<DeckWithDeckSummary> = emptyList(),
    val gridSize: Int = 2,
    val selectedDecksIds: Set<Long> = emptySet(),
    val showDeckBottomSheet: Boolean = false,
    val selectedDeckId: Long = 0

)

sealed class DecksEvent {
    data class OnSettings(val navController: NavController): DecksEvent()
    data class OnDeckClick(val deckId: Long, val navController: NavController): DecksEvent()
    data class OnDeckLongClick(val deckId: Long, val selectedChange: Boolean): DecksEvent()
    data class OnDeckFavouriteClick(val deckId: Long, val favouriteChange: Boolean): DecksEvent()
    data class OnAddDeck(val navController: NavController): DecksEvent()
    data class OnDeckMoreClick(val deckId: Long): DecksEvent()
    data class OnDeckBottomSheetEvent(val event: DeckBottomSheetEvent): DecksEvent()
}
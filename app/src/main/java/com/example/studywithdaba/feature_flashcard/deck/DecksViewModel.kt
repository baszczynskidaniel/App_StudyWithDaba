package com.example.studywithdaba.feature_flashcard.deck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
            is DecksEvent.OnDeckLongClick -> TODO()
            is DecksEvent.OnDeckMoreClick -> TODO()
            is DecksEvent.OnSettings -> {
                event.navController.navigate(Screen.Settings.route)
            }
        }
    }

}
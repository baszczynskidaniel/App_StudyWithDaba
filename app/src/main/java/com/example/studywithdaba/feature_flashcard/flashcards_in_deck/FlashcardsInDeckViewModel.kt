package com.example.studywithdaba.feature_flashcard.flashcards_in_deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardsInDeckViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository,
    savedStateHandle: SavedStateHandle

): ViewModel() {
    private val deckId: Long = savedStateHandle.get<Long>("deckId") ?: -1L
    private val _flashcards = flashcardRepository.getFlashcardsInDeck(deckId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList()
    )
    private val _state = MutableStateFlow(FlashcardsInDeckState())

    val state = combine(_state, _flashcards) {
        state, flashcards -> state.copy(
            flashcards = flashcards
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), FlashcardsInDeckState())

    init {
        viewModelScope.launch {

            val deck = deckRepository.getDeckWithDeckSummaryById(deckId)
            _state.update { it.copy(
                deckTitle = deck.deck.title,
                numberOfFlashcardToRepeat = deck.numberOfToRepeatFlashcards + deck.numberOfNewFlashcards
            ) }


        }
    }
    fun onEvent(event: FlashcardsInDeckEvent) {
        when(event) {
            is FlashcardsInDeckEvent.OnAddFlashcard -> {
                event.navController.navigate(Screen.AddFlashcard.route + "?deckId=${deckId}")
            }
            is FlashcardsInDeckEvent.OnBack -> {
                event.navController.navigate(Screen.Decks.route)
            }
            is FlashcardsInDeckEvent.OnFavouriteFlashcard -> {
                viewModelScope.launch {
                    flashcardRepository.updateFlashcardFavourite(event.flashcardId, event.favouriteChange)
                }
            }
            is FlashcardsInDeckEvent.OnMenuFlashcard -> {

            }
            is FlashcardsInDeckEvent.OnOneSideFlashcardClick -> {
                val visibleBackFlashcardIds = _state.value.visibleBackFlashcardIds.toMutableSet()
                if(event.flashcardId in visibleBackFlashcardIds)
                    visibleBackFlashcardIds.remove(event.flashcardId)
                else
                    visibleBackFlashcardIds.add(event.flashcardId)
                _state.update { it.copy(
                    visibleBackFlashcardIds = visibleBackFlashcardIds
                ) }
            }
            is FlashcardsInDeckEvent.OnQuiz -> {
                event.navController.navigate(Screen.Quiz.route + "?deckId=${deckId}")
            }
            is FlashcardsInDeckEvent.OnRepeat -> {
                event.navController.navigate(Screen.FlashcardsRepeat.route + "?deckId=${deckId}")
            }
            is FlashcardsInDeckEvent.OnReview -> {
                event.navController.navigate(Screen.FlashcardsReview.route + "?deckId=${deckId}")
            }
        }
    }
}

data class FlashcardsInDeckState(
    val deckTitle: String = "",
    val visibleBackFlashcardIds: Set<Long> = emptySet(),
    val flashcards: List<Flashcard> = emptyList(),
    val numberOfFlashcardToRepeat: Int = 0,
)

sealed class FlashcardsInDeckEvent {
    data class OnBack(val navController: NavController): FlashcardsInDeckEvent()
    data class OnRepeat(val navController: NavController): FlashcardsInDeckEvent()
    data class OnQuiz(val navController: NavController): FlashcardsInDeckEvent()
    data class OnReview(val navController: NavController): FlashcardsInDeckEvent()
    data class OnAddFlashcard(val navController: NavController): FlashcardsInDeckEvent()
    data class OnMenuFlashcard(val flashcardId: Long): FlashcardsInDeckEvent()
    data class OnOneSideFlashcardClick(val flashcardId: Long): FlashcardsInDeckEvent()
    data class OnFavouriteFlashcard(val flashcardId: Long, val favouriteChange: Boolean): FlashcardsInDeckEvent()

}
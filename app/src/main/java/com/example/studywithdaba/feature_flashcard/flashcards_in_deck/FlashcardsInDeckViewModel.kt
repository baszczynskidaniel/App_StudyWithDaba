package com.example.studywithdaba.feature_flashcard.flashcards_in_deck

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
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
        when (event) {
            is FlashcardsInDeckEvent.OnAddFlashcard -> {
                event.navController.navigate(Screen.AddFlashcard.route + "?deckId=${deckId}")
            }

            is FlashcardsInDeckEvent.OnBack -> {
                event.navController.navigate(Screen.Decks.route)
            }

            is FlashcardsInDeckEvent.OnFavouriteFlashcard -> {
                viewModelScope.launch {
                    flashcardRepository.updateFlashcardFavourite(
                        event.flashcardId,
                        event.favouriteChange
                    )
                }
            }

            is FlashcardsInDeckEvent.OnMenuFlashcard -> {
                _state.update {
                    it.copy(
                        showFlashcardBottomSheet = true,
                        bottomSheetFlashcardId = event.flashcardId
                    )
                }
            }

            is FlashcardsInDeckEvent.OnOneSideFlashcardClick -> {
                val visibleBackFlashcardIds = _state.value.visibleBackFlashcardIds.toMutableSet()
                if (event.flashcardId in visibleBackFlashcardIds)
                    visibleBackFlashcardIds.remove(event.flashcardId)
                else
                    visibleBackFlashcardIds.add(event.flashcardId)
                _state.update {
                    it.copy(
                        visibleBackFlashcardIds = visibleBackFlashcardIds
                    )
                }
            }

            is FlashcardsInDeckEvent.OnQuiz -> {
                if(_flashcards.value.size > 3) {
                    event.navController.navigate(Screen.Quiz.route + "?deckId=${deckId}")
                }
            }

            is FlashcardsInDeckEvent.OnRepeat -> {
                val numberOfFlashcardToReview = _flashcards.value.count { it.nextRepetition <= System.currentTimeMillis() }
                if(numberOfFlashcardToReview > 0) {
                    event.navController.navigate(Screen.FlashcardsRepeat.route + "?deckId=${deckId}")
                }
            }

            is FlashcardsInDeckEvent.OnReview -> {

                if(_flashcards.value.isNotEmpty()) {
                    event.navController.navigate(Screen.FlashcardsReview.route + "?deckId=${deckId}")
                }
            }

            is FlashcardsInDeckEvent.OnFlashcardBottomSheetEvent -> {
                when (event.event) {
                    FlashcardBottomSheetEvent.OnDismiss -> _state.update {
                        it.copy(
                            showFlashcardBottomSheet = false
                        )
                    }

                    is FlashcardBottomSheetEvent.OnEditFlashcard -> {
                        _state.update {
                            it.copy(
                                showFlashcardBottomSheet = false
                            )
                        }
                        event.event.navController.navigate(Screen.EditFlashcard.route + "?flashcardId=${event.event.flashcardId}")
                    }

                    is FlashcardBottomSheetEvent.OnRemoveFlashcard -> {
                        _state.update {
                            it.copy(
                                showFlashcardBottomSheet = false
                            )
                        }

                        viewModelScope.launch {
                            val decks = deckRepository.getDecksInFlashcard(event.event.flashcardId).first().toMutableList()
                            if(decks.size <= 1) {
                                flashcardRepository.removeFlashcardById(event.event.flashcardId)
                            } else {
                                decks.removeIf { it.deckId == deckId }
                                flashcardRepository.removeDeckFlashcardCrossRefsForFlashcardId(event.event.flashcardId)
                                val deckFlashcardCrossRefs = decks.map {
                                    DeckFlashcardCrossRef(it.deckId, event.event.flashcardId)
                                }
                                flashcardRepository.insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs)
                            }

                        }
                    }
                }
            }
        }
    }
}

data class FlashcardsInDeckState(
    val bottomSheetFlashcardId: Long  = 0,
    val deckTitle: String = "",
    val visibleBackFlashcardIds: Set<Long> = emptySet(),
    val flashcards: List<Flashcard> = emptyList(),
    val numberOfFlashcardToRepeat: Int = 0,
    val showFlashcardBottomSheet: Boolean = false,
)

sealed class FlashcardsInDeckEvent {
    data class OnFlashcardBottomSheetEvent(val event: FlashcardBottomSheetEvent): FlashcardsInDeckEvent()
    data class OnBack(val navController: NavController): FlashcardsInDeckEvent()
    data class OnRepeat(val navController: NavController): FlashcardsInDeckEvent()
    data class OnQuiz(val navController: NavController): FlashcardsInDeckEvent()
    data class OnReview(val navController: NavController): FlashcardsInDeckEvent()
    data class OnAddFlashcard(val navController: NavController): FlashcardsInDeckEvent()
    data class OnMenuFlashcard(val flashcardId: Long): FlashcardsInDeckEvent()
    data class OnOneSideFlashcardClick(val flashcardId: Long): FlashcardsInDeckEvent()
    data class OnFavouriteFlashcard(val flashcardId: Long, val favouriteChange: Boolean): FlashcardsInDeckEvent()

}
package com.example.studywithdaba.feature_flashcard.add_flashcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFlashcardViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val validateFlashcardDecks: ValidateFlashcardDecks = ValidateFlashcardDecks()
    private val validateFlashcardAnswer = ValidateFlashcardAnswer()
    private val validateFlashcardDefinition = ValidateFlashcardDefinition()
    private val _validationEvent = Channel<AddEditFlashcardViewModel.ValidationEvent>()
    val validationEvent = _validationEvent.receiveAsFlow()

    private val deckId = savedStateHandle.get<Long>("deckId") ?: -1
    private val _state = MutableStateFlow(AddFlashcardState())
    val state: StateFlow<AddFlashcardState> = _state

    init {
        viewModelScope.launch {
            val decks = deckRepository.getDecks().first()
            var selectedDecks: Set<Deck> = emptySet()
            if (deckId != -1L) {
                selectedDecks = setOf(deckRepository.getDeckById(deckId)!!)
                _state.update {
                    it.copy(
                        decks = decks,
                        selectedDecks = selectedDecks
                    )
                }
            }
        }
    }

    fun onEvent(event: AddFlashcardEvent) {
        when (event) {
            AddFlashcardEvent.OnAddFlashcard -> validateFlashcard()
            is AddFlashcardEvent.OnAnswerChange -> _state.update {
                it.copy(
                    answer = event.answerChange,
                    answerError = null
                )
            }

            AddFlashcardEvent.OnAnswerClear -> _state.update {
                it.copy(
                    answer = "",
                    answerError = null
                )
            }

            is AddFlashcardEvent.OnAutoMoveChange -> _state.update {
                it.copy(
                    autoMove = event.autoMoveChange
                )
            }

            is AddFlashcardEvent.OnBack -> {
                event.navController.navigateUp()
            }

            is AddFlashcardEvent.OnDefinitionChange -> _state.update {
                it.copy(
                    definition = event.definitionChange,
                    definitionError = null
                )
            }

            AddFlashcardEvent.OnDefinitionClear -> _state.update {
                it.copy(
                    definition = "",
                    definitionError = null
                )
            }

            is AddFlashcardEvent.OnFavouriteChange -> _state.update {
                it.copy(
                    favourite = event.favouriteChange
                )
            }

            is AddFlashcardEvent.OnFilterDeckClick -> {
                val selectedDecks = _state.value.selectedDecks.toMutableSet()
                if (event.selectedChange)
                    selectedDecks.remove(event.deck)
                else
                    selectedDecks.add(event.deck)
                _state.update {
                    it.copy(
                        selectedDecks = selectedDecks,
                        deckError = null,
                    )
                }
            }

            is AddFlashcardEvent.OnInputDeckClick -> {
                val selectedDecks = _state.value.selectedDecks.toMutableSet()
                selectedDecks.remove(event.deck)
                _state.update {
                    it.copy(
                        selectedDecks = selectedDecks,
                        deckError = null
                    )
                }
            }

            is AddFlashcardEvent.OnShowAllDecksChange -> _state.update {
                it.copy(
                    showAllDecks = event.onShowAllDecksChange,
                )
            }

            AddFlashcardEvent.OnSwap -> _state.update {
                it.copy(
                    definition = it.answer,
                    answer = it.definition,
                    answerError = null,
                    definitionError = null
                )
            }
        }
    }

    private fun validateFlashcard() {
        val validateAnswer = validateFlashcardAnswer.execute(_state.value.answer)
        val validateDefinition = validateFlashcardDefinition.execute(_state.value.definition)
        val validateDecks = validateFlashcardDecks.execute(_state.value.selectedDecks)

        if (validateAnswer.successful && validateDecks.successful && validateDefinition.successful) {
            viewModelScope.launch {
                val addedFlashcardId = flashcardRepository.insertFlashcard(
                    Flashcard(
                        front = _state.value.definition,
                        back = _state.value.answer,
                        favourite = _state.value.favourite
                    )
                )
                flashcardRepository.removeDeckFlashcardCrossRefsForFlashcardId(addedFlashcardId)
                val deckFlashcardCrossRefs = _state.value.selectedDecks.map {
                    DeckFlashcardCrossRef(it.deckId, addedFlashcardId)
                }
                flashcardRepository.insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs)
                _state.update {
                    it.copy(
                        definition = "",
                        answer = "",
                        favourite = false,
                    )
                }
                _validationEvent.send(AddEditFlashcardViewModel.ValidationEvent.Success)
            }
        } else {
            _state.update {
                it.copy(
                    answerError = validateAnswer.errorMessage,
                    definitionError = validateDefinition.errorMessage,
                    deckError = validateDecks.errorMessage
                )
            }
        }
    }
}

sealed class ValidationEvent {
    object Success: ValidationEvent()
}
data class AddFlashcardState(
    val definition: String = "",
    val answer: String = "",
    val definitionError: String? = null,
    val answerError: String? = null,
    val autoMove: Boolean = true,
    val showAllDecks: Boolean = false,
    val favourite: Boolean = false,
    val deckError: String? = null,
    val decks: List<Deck> = emptyList(),
    val selectedDecks: Set<Deck> = emptySet()

)

sealed class AddFlashcardEvent {
    data class OnBack(val navController: NavController): AddFlashcardEvent()
    data class OnFavouriteChange(val favouriteChange: Boolean): AddFlashcardEvent()
    data class OnDefinitionChange(val definitionChange: String): AddFlashcardEvent()
    data class OnAnswerChange(val answerChange: String): AddFlashcardEvent()
    object OnDefinitionClear: AddFlashcardEvent()
    object OnAnswerClear: AddFlashcardEvent()
    object OnSwap: AddFlashcardEvent()
    data class OnAutoMoveChange(val autoMoveChange: Boolean): AddFlashcardEvent()
    data class OnFilterDeckClick(val deck: Deck, val selectedChange: Boolean): AddFlashcardEvent()
    data class OnInputDeckClick(val deck: Deck): AddFlashcardEvent()
    data class OnShowAllDecksChange(val onShowAllDecksChange: Boolean): AddFlashcardEvent()
    object OnAddFlashcard: AddFlashcardEvent()
}
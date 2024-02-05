package com.example.studywithdaba.feature_flashcard.edit_flashcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import com.example.studywithdaba.feature_flashcard.add_flashcard.AddEditFlashcardViewModel
import com.example.studywithdaba.feature_flashcard.add_flashcard.ValidateFlashcardAnswer
import com.example.studywithdaba.feature_flashcard.add_flashcard.ValidateFlashcardDecks
import com.example.studywithdaba.feature_flashcard.add_flashcard.ValidateFlashcardDefinition
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
class EditFlashcardViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val validateFlashcardDecks: ValidateFlashcardDecks = ValidateFlashcardDecks()
    private val validateFlashcardAnswer = ValidateFlashcardAnswer()
    private val validateFlashcardDefinition = ValidateFlashcardDefinition()
    private val _validationEvent = Channel<AddEditFlashcardViewModel.ValidationEvent>()
    val validationEvent = _validationEvent.receiveAsFlow()

    private val flashcardId = savedStateHandle.get<Long>("flashcardId") ?: -1
    private val _state = MutableStateFlow(EditFlashcardState())
    val state: StateFlow<EditFlashcardState> = _state

    init {
        viewModelScope.launch {
            val flashcard = flashcardRepository.getFlashcardById(flashcardId)!!
            val selectedDecks = deckRepository.getDecksInFlashcard(flashcardId).first().toSet()
            val decks = deckRepository.getDecks().first()



            if (flashcardId != -1L) {

                _state.update {
                    it.copy(
                        decks = decks,
                        selectedDecks = selectedDecks,
                        definition = flashcard.front,
                        answer = flashcard.back,
                        favourite = flashcard.favourite
                    )
                }
            }
        }
    }

    fun onEvent(event: EditFlashcardEvent) {
        when (event) {
            is EditFlashcardEvent.OnApplyChanges -> {
                validateFlashcard(event.navController)

            }
            is EditFlashcardEvent.OnAnswerChange -> _state.update {
                it.copy(
                    answer = event.answerChange,
                    answerError = null
                )
            }

            EditFlashcardEvent.OnAnswerClear -> _state.update {
                it.copy(
                    answer = "",
                    answerError = null
                )
            }

            is EditFlashcardEvent.OnAutoMoveChange -> _state.update {
                it.copy(
                    autoMove = event.autoMoveChange
                )
            }

            is EditFlashcardEvent.OnBack -> {
                event.navController.navigateUp()
            }

            is EditFlashcardEvent.OnDefinitionChange -> _state.update {
                it.copy(
                    definition = event.definitionChange,
                    definitionError = null
                )
            }

            EditFlashcardEvent.OnDefinitionClear -> _state.update {
                it.copy(
                    definition = "",
                    definitionError = null
                )
            }

            is EditFlashcardEvent.OnFavouriteChange -> _state.update {
                it.copy(
                    favourite = event.favouriteChange
                )
            }

            is EditFlashcardEvent.OnFilterDeckClick -> {
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

            is EditFlashcardEvent.OnInputDeckClick -> {
                val selectedDecks = _state.value.selectedDecks.toMutableSet()
                selectedDecks.remove(event.deck)
                _state.update {
                    it.copy(
                        selectedDecks = selectedDecks,
                        deckError = null
                    )
                }
            }

            is EditFlashcardEvent.OnShowAllDecksChange -> _state.update {
                it.copy(
                    showAllDecks = event.onShowAllDecksChange,
                )
            }

            EditFlashcardEvent.OnSwap -> _state.update {
                it.copy(
                    definition = it.answer,
                    answer = it.definition,
                    answerError = null,
                    definitionError = null
                )
            }
        }
    }

    private fun validateFlashcard(navController: NavController) {
        val validateAnswer = validateFlashcardAnswer.execute(_state.value.answer)
        val validateDefinition = validateFlashcardDefinition.execute(_state.value.definition)
        val validateDecks = validateFlashcardDecks.execute(_state.value.selectedDecks)

        if (validateAnswer.successful && validateDecks.successful && validateDefinition.successful) {
            viewModelScope.launch {
                flashcardRepository.updateFlashcard(
                    flashcardId = flashcardId,
                    definition = _state.value.definition,
                    answer = _state.value.answer
                )
                flashcardRepository.updateFlashcardFavourite(flashcardId, _state.value.favourite)

                flashcardRepository.removeDeckFlashcardCrossRefsForFlashcardId(flashcardId)
                val deckFlashcardCrossRefs = _state.value.selectedDecks.map {
                    DeckFlashcardCrossRef(it.deckId, flashcardId)
                }
                flashcardRepository.insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs)
                _validationEvent.send(AddEditFlashcardViewModel.ValidationEvent.Success)

            }
            navController.navigateUp()
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
data class EditFlashcardState(
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

sealed class EditFlashcardEvent {
    data class OnBack(val navController: NavController): EditFlashcardEvent()
    data class OnFavouriteChange(val favouriteChange: Boolean): EditFlashcardEvent()
    data class OnDefinitionChange(val definitionChange: String): EditFlashcardEvent()
    data class OnAnswerChange(val answerChange: String): EditFlashcardEvent()
    object OnDefinitionClear: EditFlashcardEvent()
    object OnAnswerClear: EditFlashcardEvent()
    object OnSwap: EditFlashcardEvent()
    data class OnAutoMoveChange(val autoMoveChange: Boolean): EditFlashcardEvent()
    data class OnFilterDeckClick(val deck: Deck, val selectedChange: Boolean): EditFlashcardEvent()
    data class OnInputDeckClick(val deck: Deck): EditFlashcardEvent()
    data class OnShowAllDecksChange(val onShowAllDecksChange: Boolean): EditFlashcardEvent()
    data class OnApplyChanges(val navController: NavController): EditFlashcardEvent()
}
package com.example.studywithdaba.feature_flashcard.add_flashcard

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.studywithdaba.MainActivity
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlashcardAndDeckId(
    val flashcardId: Long?,
    val deckId: Long?,
)
class AddEditFlashcardViewModel @AssistedInject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository,

    @Assisted private val flashcardAndDeckId: FlashcardAndDeckId,
): ViewModel() {
    private val _state = MutableStateFlow(AddEditFlashcardState())
    val state = _state
    private val validateDefinition = ValidateDefinition()
    private val validateAnswer = ValidateAnswer()
    private val validateDecks = ValidateDecks()

    private val _validationEvent = Channel<ValidationEvent>()
    val validationEvent = _validationEvent.receiveAsFlow()

    private val flashcardId: Long?
    private val deckId: Long?
    init {
        flashcardId = flashcardAndDeckId.flashcardId
        deckId = flashcardAndDeckId.deckId
        when {


            flashcardId == null && deckId != null -> {
                viewModelScope.launch {
                    val selectedDeck = deckRepository.getDeckById(deckId)
                    val selectedDeckTitleAndId = DeckTitleWithDeckId.getFromDeck(selectedDeck!!)
                    val allDecks = deckRepository.getDecks().first().map { DeckTitleWithDeckId(it.deckId, it.title) }
                    _state.update {
                        it.copy(
                            label = "Add flashcard",
                            selectedDecks = setOf(selectedDeckTitleAndId),
                            deckTitlesWithIds = allDecks
                        )
                    }
                }
            }

            flashcardId == null && deckId == null -> {
                viewModelScope.launch {
                    val allDecks = deckRepository.getDecks().first().map { DeckTitleWithDeckId(it.deckId, it.title) }
                    _state.update {
                        it.copy(
                            label = "Add flashcard",
                            deckTitlesWithIds = allDecks
                        )
                    }
                }

            }

            else -> {
                viewModelScope.launch {
                    val flashcard = flashcardRepository.getFlashcardById(flashcardId!!)!!
                    val decks = deckRepository.getDecksInFlashcard(flashcardId).first()
                    val deckTitlesWithIds = DeckTitleWithDeckId.getListFromDecks(decks).toSet()
                    val allDecks = deckRepository.getDecks().first().map { DeckTitleWithDeckId(it.deckId, it.title) }
                    _state.update {
                        it.copy(
                            label = "Update flashcard",
                            selectedDecks = deckTitlesWithIds,
                            deckTitlesWithIds = allDecks,
                            definition = flashcard.front,
                            answer = flashcard.back,

                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: AddEditFlashcardEvent) {
        when(event) {
            is AddEditFlashcardEvent.OnAnswerChange -> _state.update { it.copy(
                answer = event.answerChange,
                answerError = null
            ) }
            AddEditFlashcardEvent.OnAnswerClear -> _state.update { it.copy(
                answer = "",
                answerError = null
            ) }
            AddEditFlashcardEvent.OnApply -> validateFlashcard()
            is AddEditFlashcardEvent.OnAutoMoveChange -> _state.update { it.copy(
                autoMove = event.valueChange
            ) }
            is AddEditFlashcardEvent.OnDeckClick -> {
                val deckTitlesWithIds = _state.value.deckTitlesWithIds.toMutableList()
                deckTitlesWithIds.removeIf { it.deckId == event.deckId }
                _state.update { it.copy(
                    deckTitlesWithIds = deckTitlesWithIds
                ) }
            }
            is AddEditFlashcardEvent.OnDeckSelection -> {
                val selectedDecks = _state.value.selectedDecks.toMutableSet()
                if(event.selectedChange)
                    selectedDecks.add(event.deck)
                else
                    selectedDecks.remove(event.deck)
                _state.update {
                    it.copy(
                        decksError = null,
                        selectedDecks = selectedDecks
                    )
                }
            }
            is AddEditFlashcardEvent.OnDefinitionChange -> _state.update { it.copy(
                definition = event.definitionChange,
                definitionError = null
            ) }
            AddEditFlashcardEvent.OnDefinitionClear -> _state.update { it.copy(
                definition = "",
                definitionError = null
            ) }
            AddEditFlashcardEvent.OnSwap -> _state.update { it.copy(
                answer = it.definition,
                definition = it.answer,
                answerError = null,
                definitionError = null,
            ) }

            is AddEditFlashcardEvent.OnShowDecks -> _state.update { it.copy(
                showDecks = event.showChange
            ) }

        }
    }

    private fun validateFlashcard() {
        val definitionValidationResult = validateDefinition.execute(_state.value.definition)
        val answerValidationResult = validateAnswer.execute(_state.value.answer)
        val decksValidationResult = validateDecks.execute(_state.value.selectedDecks)

        if(definitionValidationResult.successful && answerValidationResult.successful && decksValidationResult.successful) {

            if(flashcardId != null) {
                viewModelScope.launch {
                    flashcardRepository.updateFlashcard(flashcardId, _state.value.definition, _state.value.answer)
                    flashcardRepository.removeDeckFlashcardCrossRefsForFlashcardId(flashcardId)
                    val deckFlashcardCrossRefs = _state.value.selectedDecks.map {
                        DeckFlashcardCrossRef(it.deckId, flashcardId)
                    }
                    flashcardRepository.insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs)
                    _validationEvent.send(ValidationEvent.Success)
                }

            } else {
                viewModelScope.launch {
                    val addedFlashcardId = flashcardRepository.insertFlashcard(Flashcard(front = _state.value.definition, back = _state.value.answer))
                    flashcardRepository.removeDeckFlashcardCrossRefsForFlashcardId(addedFlashcardId)
                    val deckFlashcardCrossRefs = _state.value.selectedDecks.map {
                        DeckFlashcardCrossRef(it.deckId, addedFlashcardId)
                    }
                    flashcardRepository.insertDeckFlashcardCrossRefs(deckFlashcardCrossRefs)
                    _state.update {
                        it.copy(
                            definition = "",
                            answer = "",
                        )
                    }
                    _validationEvent.send(ValidationEvent.Success)
                }
            }
        } else {
            _state.update { it.copy(
                answerError = answerValidationResult.errorMessage,
                definitionError = definitionValidationResult.errorMessage,
                decksError = decksValidationResult.errorMessage
            )
            }
        }
    }

    sealed class ValidationEvent {
        object Success: ValidationEvent()
    }

    @AssistedFactory
    interface Factory {
        fun create(flashcardAndDeckId: FlashcardAndDeckId): AddEditFlashcardViewModel
    }
    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            flashcardAndDeckId: FlashcardAndDeckId
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(flashcardAndDeckId) as T
            }
        }
    }
}

@Composable
fun addEditFlashcardViewModel(flashcardAndDeckId: FlashcardAndDeckId): AddEditFlashcardViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).addEditFlashcardViewModel()

    return viewModel(factory = AddEditFlashcardViewModel.provideFactory(factory, flashcardAndDeckId))
}




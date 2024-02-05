package com.example.studywithdaba.feature_flashcard.flashcards_review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardRepeatedVisibility
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardsReviewSettingsState
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryEvent
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FlashcardsReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val flashcardRepository: FlashcardRepository
): ViewModel() {

    private val _flashcards = flashcardRepository.getFlashcardsInDeck(savedStateHandle.get<Long>("deckId")!!).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    private val _state = MutableStateFlow(FlashcardsReviewState())
    val state = combine(_state, _flashcards) {
        state, flashcards -> state.copy(
            flashcards = flashcards
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FlashcardsReviewState())

    init {
        val deckId = savedStateHandle.get<Long>("deckId")!!
        if(deckId != -1L)
        viewModelScope.launch {
            val flashcards = flashcardRepository.getFlashcardsInDeck(deckId).first()
            val flashcardsInReview = generateFlashcardsInReview(flashcards)
            _state.value = _state.value.copy(
                flashcards = _flashcards.value,
                deckId = deckId,
                flashcardsInReview = flashcardsInReview,
                currentFlashcard = flashcardsInReview.first(),
                isCurrentFlashcardFrontVisible = flashcardsInReview.first().isFrontVisible
            )
        }
        else {
            _state.value = _state.value.copy(
                showSummaryDialog = true
            )
        }
    }

    private fun generateFlashcardsInReview(flashcards: List<Flashcard>): List<FlashcardInReview> {
        if(_state.value.settings.infiniteMode) {
            return generateInfiniteFlashcardsInReview(flashcards)
        } else {
            return generateLimitedFlashcardsInReview(flashcards)
        }
    }

    private fun generateLimitedFlashcardsInReview(flashcards: List<Flashcard>): List<FlashcardInReview> {
        val settings = state.value.settings
        val flashcardsInGeneration = flashcards.filter { if(settings.onlyFavourite) it.favourite else true}.toMutableList()
        val flashcardAnswered = flashcardsInGeneration.map { flashcard ->
            FlashcardInReview(
                flashcard,
                when(settings.visibility) {
                    FlashcardRepeatedVisibility.FRONT -> true
                    FlashcardRepeatedVisibility.BACK -> false
                    FlashcardRepeatedVisibility.RANDOM -> Random.nextBoolean()
                }
            )
        }.toMutableList()
        if(settings.shuffle)
            flashcardAnswered.shuffle()
        return flashcardAnswered
    }

    private fun generateInfiniteFlashcardsInReview(flashcards: List<Flashcard>): List<FlashcardInReview> {
        val settings = state.value.settings
        val flashcardsInGeneration = flashcards.filter { if(settings.onlyFavourite) it.favourite else true}.toMutableList()

        if(flashcardsInGeneration.isEmpty())
            return emptyList()

        val flashcardsInReview = flashcardsInGeneration.map { flashcard ->
            val visibility = when (settings.visibility) {
                FlashcardRepeatedVisibility.FRONT -> true
                FlashcardRepeatedVisibility.BACK -> false
                FlashcardRepeatedVisibility.RANDOM -> Random.nextBoolean()
            }
            if (settings.shuffle) {
                FlashcardInReview(
                    flashcards.random(),
                    visibility
                )
            } else {
                FlashcardInReview(
                    flashcard,
                    visibility
                )
            }
        }
        return flashcardsInReview
    }

    fun onEvent(event: FlashcardsReviewEvent) {
        when(event) {
            is FlashcardsReviewEvent.OnBack -> onBack(event.navController)
            FlashcardsReviewEvent.OnCorrectAnswer -> onAnswer(true)
            is FlashcardsReviewEvent.OnFavouriteChange -> onFavouriteClick(event.value)
            FlashcardsReviewEvent.OnFinishReview -> onFinishReview()
            FlashcardsReviewEvent.OnLastCard -> onLastCard()
            FlashcardsReviewEvent.OnWrongAnswer -> onAnswer(false)
            is FlashcardsReviewEvent.OnSettings -> onSettings()
            is FlashcardsReviewEvent.OnSettingsDismiss -> onSettingsDismiss(event.newSettings)
            is FlashcardsReviewEvent.OnFlashcardClick -> onFlashcardClick()
            is FlashcardsReviewEvent.OnSummaryDialogEvent -> onSummaryDialogEvent(event.event, event.navController)
        }
    }

    private fun onSummaryDialogEvent(event: StudySummaryEvent, navController: NavController) {
        when(event) {
            StudySummaryEvent.OnDismissRequest -> {
                val removedLastAnswer = _state.value.answeredFlashcards.pop()
                _state.value = _state.value.copy(
                    numberOfAnsweredFlashcards = _state.value.numberOfAnsweredFlashcards - 1,
                    reviewSummary = _state.value.reviewSummary.getSummaryWithPopAnswer(removedLastAnswer.isAnswerCorrect),
                    showSummaryDialog = false
                )
            }
            StudySummaryEvent.OnDoneSummary -> {
                navController.navigateUp()
            }
            StudySummaryEvent.OnTryAgain -> {
                val flashcardsInReview = generateFlashcardsInReview(_flashcards.value)
                _state.value = _state.value.copy(
                    flashcardsInReview = flashcardsInReview,
                    currentFlashcard = flashcardsInReview.first(),
                    isCurrentFlashcardFrontVisible = flashcardsInReview.first().isFrontVisible,
                    answeredFlashcards = Stack(),
                    reviewSummary = StudySummaryState(),
                    numberOfAnsweredFlashcards = 0,
                    showSettingsDialog = false,
                    showSummaryDialog = false,
                )
            }
        }
    }

    private fun onFinishReview() {
        _state.value = _state.value.copy(
            showSummaryDialog = true
        )
    }

    private fun onSettingsDismiss(newSettings: FlashcardsReviewSettingsState) {
        if(_state.value.settings != newSettings) {
            _state.value = _state.value.copy(settings = newSettings)
            val flashcardsInReview = generateFlashcardsInReview(_flashcards.value)
            if(flashcardsInReview.size != 0) {
                _state.value = _state.value.copy(

                    flashcardsInReview = flashcardsInReview,
                    currentFlashcard = flashcardsInReview.first(),
                    isCurrentFlashcardFrontVisible = flashcardsInReview.first().isFrontVisible,
                    answeredFlashcards = Stack(),
                    reviewSummary = StudySummaryState(),
                    numberOfAnsweredFlashcards = 0,
                    showSettingsDialog = false
                )
            } else {
                _state.value = _state.value.copy(
                    showSettingsDialog = false
                )
            }
        } else {
            _state.value = _state.value.copy(
                showSettingsDialog = false
            )
        }
    }

    private fun onSettings() {
        _state.value = _state.value.copy(
            showSettingsDialog = !_state.value.showSettingsDialog
        )
    }

    private fun onFlashcardClick() {
        _state.value = _state.value.copy(
            isCurrentFlashcardFrontVisible = !_state.value.isCurrentFlashcardFrontVisible
        )
    }

    private fun onFavouriteClick(favourite: Boolean) {
        viewModelScope.launch {
            flashcardRepository.updateFlashcardFavourite(_state.value.currentFlashcard!!.flashcard.flashcardId, !favourite)
        }
    }


    private fun onBack(navController: NavController) {
        navController.navigateUp()
    }

    private fun getNextFlashcardInReview(): FlashcardInReview? {
        val nextIndex = _state.value.numberOfAnsweredFlashcards
        if(nextIndex >= _state.value.flashcardsInReview.size)
            return null
        return _state.value.flashcardsInReview[nextIndex]
    }


    private fun onLastCard() {
        if(_state.value.answeredFlashcards.isEmpty())
            return
        val removedFlashcardAnswer = _state.value.answeredFlashcards.pop()

        val newCurrentFlashcard = _state.value.flashcardsInReview[_state.value.numberOfAnsweredFlashcards - 1]
        _state.value = _state.value.copy(
            isCurrentFlashcardFrontVisible = newCurrentFlashcard.isFrontVisible,
            currentFlashcard = newCurrentFlashcard,
            numberOfAnsweredFlashcards = _state.value.numberOfAnsweredFlashcards - 1,
            reviewSummary = _state.value.reviewSummary.getSummaryWithPopAnswer(removedFlashcardAnswer.isAnswerCorrect)
        )
    }

    private fun onAnswer(isCorrect: Boolean) {
        _state.value.answeredFlashcards.add(FlashcardAnswer(_state.value.currentFlashcard!!, isCorrect))
        _state.value = _state.value.copy(
            numberOfAnsweredFlashcards = _state.value.numberOfAnsweredFlashcards + 1,
            reviewSummary = _state.value.reviewSummary.getSummaryWithNewAnswer(isCorrect)
        )

        val nextFlashcardReview = getNextFlashcardInReview()
        if(nextFlashcardReview == null) {
            if(_state.value.settings.infiniteMode) {
                val newGeneratedFlashcards = generateInfiniteFlashcardsInReview(_state.value.flashcards)
                _state.value = _state.value.copy(
                    flashcardsInReview = _state.value.flashcardsInReview + newGeneratedFlashcards,
                    currentFlashcard = newGeneratedFlashcards.first(),
                    isCurrentFlashcardFrontVisible = newGeneratedFlashcards.first().isFrontVisible,
                )
            } else {
                _state.value = _state.value.copy(
                    numberOfAnsweredFlashcards = _state.value.numberOfAnsweredFlashcards - 1,
                    showSummaryDialog = true
                )
            }
        } else {
            _state.value = _state.value.copy(
                currentFlashcard = nextFlashcardReview,
                isCurrentFlashcardFrontVisible = nextFlashcardReview.isFrontVisible,
            )
        }
    }
}
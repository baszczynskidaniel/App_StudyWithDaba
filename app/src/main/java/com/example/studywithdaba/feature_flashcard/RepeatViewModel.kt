package com.example.studywithdaba.feature_flashcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.FlashcardStatus
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import com.example.studywithdaba.feature_flashcard.flashcards_review.FlashcardInReview
import com.example.studywithdaba.feature_flashcard.flashcards_review.FlashcardsReviewEvent
import com.example.studywithdaba.feature_flashcard.flashcards_review.FlashcardsReviewState
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryEvent
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RepeatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val flashcardRepository: FlashcardRepository
): ViewModel() {

    private val _state = MutableStateFlow(RepeatState())
    val state: StateFlow<RepeatState> = _state


    init {
        viewModelScope.launch {
            val flashcards =
                flashcardRepository.getFlashcardsInDeck(savedStateHandle.get<Long>("deckId")!!)
                    .first()
            val flashcardsToRepeat = mutableListOf<Flashcard>()
            flashcards.forEach {
                if (it.state != FlashcardStatus.REPEATED || it.nextRepetition < System.currentTimeMillis())
                    flashcardsToRepeat.add(it)
            }
            _state.update { it.copy(
                flashcards = flashcardsToRepeat,
                currentFlashcard = flashcardsToRepeat.first(),

            )
            }
        }
    }

    fun onEvent(event: RepeatEvent) {
        when(event) {
            is RepeatEvent.OnBack -> {
                event.navController.navigateUp()
            }
            RepeatEvent.OnCorrectAnswer -> {
                val newScore = _state.value.currentFlashcard.getIncrementedScore()
                val nextRepetition = _state.value.currentFlashcard.getNextRepetitionTimeWithScoreChange(1)
                val currentFlashcard = _state.value.currentFlashcard
                viewModelScope.launch {
                    flashcardRepository.updateFlashcardRepetition(currentFlashcard.flashcardId, nextRepetition, FlashcardStatus.REPEATED, newScore)
                }
                val isFlashcardLast = _state.value.numberOfAnsweredFlashcards + 1 >= _state.value.flashcards.size
                if(isFlashcardLast)
                _state.update { it.copy(
                    numberOfAnsweredFlashcards = it.numberOfAnsweredFlashcards + 1,
                    showSummaryDialog = true,
                    reviewSummary = it.reviewSummary.getSummaryWithNewAnswer(true)
                ) }
                else
                _state.update { it.copy(
                    numberOfAnsweredFlashcards = it.numberOfAnsweredFlashcards + 1,
                    isCurrentFlashcardFrontVisible = true,
                    currentFlashcard = it.flashcards[it.numberOfAnsweredFlashcards + 1],
                    reviewSummary = it.reviewSummary.getSummaryWithNewAnswer(true)
                ) }

            }
            is RepeatEvent.OnFlashcardClick -> {
                _state.update {it.copy(
                    isCurrentFlashcardFrontVisible = !it.isCurrentFlashcardFrontVisible
                )
                }
            }
            is RepeatEvent.OnSummaryDialogEvent -> {
                val navController = event.navController
                when(event.event) {
                    StudySummaryEvent.OnDismissRequest -> {
                        navController.navigateUp()
                    }
                    StudySummaryEvent.OnDoneSummary -> {
                        navController.navigateUp()
                    }
                    StudySummaryEvent.OnTryAgain -> {
                        navController.navigateUp()
                    }
                }
            }
            RepeatEvent.OnWrongAnswer -> {
                val newScore = _state.value.currentFlashcard.getDecrementedScore()
                val nextRepetition = _state.value.currentFlashcard.getNextRepetitionTimeWithScoreChange(-1)
                val currentFlashcard = _state.value.currentFlashcard
                viewModelScope.launch {
                    flashcardRepository.updateFlashcardRepetition(currentFlashcard.flashcardId, nextRepetition, FlashcardStatus.REPEATED, newScore)
                }
                val isFlashcardLast = _state.value.numberOfAnsweredFlashcards + 1 >= _state.value.flashcards.size
                if(isFlashcardLast)
                    _state.update { it.copy(
                        reviewSummary = it.reviewSummary.getSummaryWithNewAnswer(false),
                        numberOfAnsweredFlashcards = it.numberOfAnsweredFlashcards + 1,
                        showSummaryDialog = true
                    ) }
                else
                    _state.update { it.copy(
                        numberOfAnsweredFlashcards = it.numberOfAnsweredFlashcards + 1,
                        isCurrentFlashcardFrontVisible = true,
                        currentFlashcard = it.flashcards[it.numberOfAnsweredFlashcards + 1],
                        reviewSummary = it.reviewSummary.getSummaryWithNewAnswer(false)
                    ) }

            }

        }
    }
}

data class RepeatState(
    val reviewSummary: StudySummaryState = StudySummaryState(0, 0),
    val flashcards: List<Flashcard> = emptyList(),
    val currentFlashcard: Flashcard = Flashcard("", ""),
    val isCurrentFlashcardFrontVisible: Boolean = true,
    val numberOfAnsweredFlashcards: Int = 0,
    val showSummaryDialog: Boolean = false,
)

sealed class RepeatEvent {
    data class OnBack(val navController: NavController): RepeatEvent()
    object OnCorrectAnswer: RepeatEvent()
    object OnWrongAnswer: RepeatEvent()
    data class OnFlashcardClick(val isFrontVisible: Boolean): RepeatEvent()
    data class OnSummaryDialogEvent(val event: StudySummaryEvent, val navController: NavController): RepeatEvent()
}
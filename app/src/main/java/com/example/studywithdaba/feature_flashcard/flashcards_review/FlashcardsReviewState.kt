package com.example.studywithdaba.feature_flashcard.flashcards_review

import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardsReviewSettingsState
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryState
import java.util.Stack

data class FlashcardsReviewState(
    val settings: FlashcardsReviewSettingsState = FlashcardsReviewSettingsState(),
    val flashcardsInReview: List<FlashcardInReview> = emptyList(),
    val flashcards: List<Flashcard> = emptyList(),
    val isCurrentFlashcardFrontVisible: Boolean = false,
    val answeredFlashcards: Stack<FlashcardAnswer> = Stack(),
    val numberOfAnsweredFlashcards: Int = 0,
    val areSettingsVisible: Boolean = false,
    val currentFlashcard: FlashcardInReview? = FlashcardInReview(Flashcard("", "",), true),
    val showSummaryDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val deckId: Long = -1,
    val reviewSummary: StudySummaryState = StudySummaryState()
)

data class FlashcardInReview(
    val flashcard: Flashcard,
    val isFrontVisible: Boolean
)

data class FlashcardAnswer(
    val flashcard: FlashcardInReview,
    val isAnswerCorrect: Boolean
)

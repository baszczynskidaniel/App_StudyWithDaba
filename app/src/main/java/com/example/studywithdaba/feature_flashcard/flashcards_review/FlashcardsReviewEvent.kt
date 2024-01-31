package com.example.studywithdaba.feature_flashcard.flashcards_review

import androidx.navigation.NavController
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardsReviewSettingsState
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryEvent


sealed class FlashcardsReviewEvent {
    data class OnFavouriteChange(val value: Boolean): FlashcardsReviewEvent()
    data class OnBack(val navController: NavController): FlashcardsReviewEvent()
    data class OnSettings(val newSettings: FlashcardsReviewSettingsState): FlashcardsReviewEvent()
    object OnCorrectAnswer: FlashcardsReviewEvent()
    object OnWrongAnswer: FlashcardsReviewEvent()
    object OnLastCard: FlashcardsReviewEvent()
    object OnFinishReview: FlashcardsReviewEvent()
    data class OnSettingsDismiss(val newSettings: FlashcardsReviewSettingsState): FlashcardsReviewEvent()
    data class OnFlashcardClick(val isFrontVisible: Boolean): FlashcardsReviewEvent()
    data class OnSummaryDialogEvent(val event: StudySummaryEvent, val navController: NavController): FlashcardsReviewEvent()
}
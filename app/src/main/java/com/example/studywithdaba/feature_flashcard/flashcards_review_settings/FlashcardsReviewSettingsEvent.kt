package com.example.studywithdaba.feature_flashcard.flashcards_review_settings

sealed class FlashcardsReviewSettingsEvent {
    data class OnShuffleChange(val value: Boolean): FlashcardsReviewSettingsEvent()
    data class OnFavouriteChange(val value: Boolean): FlashcardsReviewSettingsEvent()
    data class OnInfinityModeChange(val value: Boolean): FlashcardsReviewSettingsEvent()
    data class OnVisibilityChange(val visibility: FlashcardRepeatedVisibility): FlashcardsReviewSettingsEvent()
}
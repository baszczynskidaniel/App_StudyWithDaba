package com.example.studywithdaba.feature_flashcard.flashcards_review_settings


data class FlashcardsReviewSettingsState(
    val shuffle: Boolean = false,
    val infiniteMode: Boolean = false,
    val onlyFavourite: Boolean = false,
    val visibility: FlashcardRepeatedVisibility = FlashcardRepeatedVisibility.FRONT,
)

enum class FlashcardRepeatedVisibility{
    FRONT,
    BACK,
    RANDOM
}
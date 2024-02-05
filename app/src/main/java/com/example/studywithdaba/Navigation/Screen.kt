package com.example.studywithdaba.Navigation

sealed  class Screen(val route: String) {
    object Home: Screen("home")
    object Notes: Screen("notes")
    object EditNote: Screen("edit_note")
    object Settings: Screen("settings")
    object EditFlashcard: Screen("edit_flashcard")
    object EditDeck:  Screen("edit_deck")

    object AddDeck: Screen("add_deck")
    object AddFlashcard: Screen("add_flashcard")
    object Quiz: Screen("quiz")
    object FlashcardsReview: Screen("flashcards_review")
    object FlashcardsRepeat: Screen("flashcards_repeat")

    object FlashcardsInDeck: Screen("flashcards_in_deck")
    object Decks: Screen("decks")
}
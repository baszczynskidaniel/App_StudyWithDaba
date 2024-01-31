package com.example.studywithdaba.feature_flashcard.add_flashcard

import com.example.dabastudy.core.database.model.entities.Deck
import com.example.studywithdaba.core.data.util.ValidationResult

class ValidateFlashcardDecks {
    fun execute(decks: Set<Deck>): ValidationResult {
        return if (decks.isEmpty()) {
            ValidationResult(
                false,
                "Flashcard must belong to at least one deck"
            )
        } else {
            ValidationResult(
                true,
                null
            )
        }
    }
}
class ValidateFlashcardDefinition {
    fun execute(definition: String): ValidationResult {
        return if (definition.isBlank()) {
            ValidationResult(
                false,
                "Definition cannot be empty"
            )
        } else {
            ValidationResult(
                true,
                null
            )
        }
    }
}

class ValidateFlashcardAnswer {
    fun execute(answer: String): ValidationResult {
        return if (answer.isBlank()) {
            ValidationResult(
                false,
                "Answer cannot be empty"
            )
        } else {
            ValidationResult(
                true,
                null
            )
        }
    }
}
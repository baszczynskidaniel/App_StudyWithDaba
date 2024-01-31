package com.example.studywithdaba.feature_flashcard.add_flashcard

import com.example.studywithdaba.core.data.util.ValidationResult



class ValidateDefinition {
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
class ValidateDecks {
    fun execute(decks: Set<DeckTitleWithDeckId>): ValidationResult {
        return if (decks.isEmpty()) {
            ValidationResult(
                false,
                "Flashcard must be in at least one deck"
            )
        } else {
            ValidationResult(
                true,
                null
            )
        }
    }
}
class ValidateAnswer {
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
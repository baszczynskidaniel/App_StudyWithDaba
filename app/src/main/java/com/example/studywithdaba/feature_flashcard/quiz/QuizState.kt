package com.example.studywithdaba.feature_flashcard.quiz

import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryState

data class QuizState(
    val quizQuestions: List<QuizQuestion> = emptyList(),
    val currentQuestion: QuizQuestion = QuizQuestion(),
    val showResult: Boolean = false,
    val progressMessage: String = "",
    val showSummary: Boolean = false,
    val answeredQuestions: Int = 0,
    val currentPickedAnswer: Int = -1,
    val isCurrentPickedAnswerCorrect: Boolean = false,
    val checkContinueButtonName: String = "CHECK",
    val summary: StudySummaryState = StudySummaryState(),
    val enableCheck: Boolean = true,

    )

data class QuizQuestion(
    val question: String = "",
    val answers: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val isQuestionForFront: Boolean = true,

) {
    fun isAnswerWithIndexCorrect(index: Int): Boolean {
        return index == correctAnswerIndex
    }
    fun getCorrectAnswerText(): String {
        return answers[correctAnswerIndex]
    }
}


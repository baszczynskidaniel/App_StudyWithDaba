package com.example.studywithdaba.feature_flashcard.quiz

import androidx.navigation.NavController
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryEvent

sealed class QuizEvent {
    data class OnBack(val navController: NavController): QuizEvent()
    data class OnClickAnswer(val answerIndex: Int): QuizEvent()
    object OnCheck: QuizEvent()
    object OnDismissResult: QuizEvent()
    data class OnSummaryDialogEvent(val event: StudySummaryEvent, val navController: NavController): QuizEvent()




}
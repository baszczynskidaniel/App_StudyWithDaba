package com.example.studywithdaba.feature_flashcard.study_summary

sealed class StudySummaryEvent {
    object OnDismissRequest: StudySummaryEvent()
    object OnTryAgain: StudySummaryEvent()
    object OnDoneSummary: StudySummaryEvent()
}
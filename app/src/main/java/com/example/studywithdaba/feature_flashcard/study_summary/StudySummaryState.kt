package com.example.studywithdaba.feature_flashcard.study_summary

data class StudySummaryState(
    val wrongNumber: Int = 0,
    val correctNumber: Int = 0,
) {
    fun getScoreMessage(): String {
        return "$correctNumber / ${correctNumber + wrongNumber}"
    }

    fun getSummaryMessage(): String {
        return "correct: $correctNumber\nwrong: $wrongNumber"
    }

    fun getSummaryWithNewAnswer(isAnswerCorrect: Boolean): StudySummaryState {
        if(isAnswerCorrect)
            return this.copy(
                correctNumber = this.correctNumber + 1,
            )
        return this.copy(
            wrongNumber = this.wrongNumber + 1,
        )
    }
    fun getSummaryWithPopAnswer(isPoppedAnswerCorrect: Boolean): StudySummaryState {
        if(isPoppedAnswerCorrect)
            return this.copy(
                correctNumber = this.correctNumber - 1,
            )
        return this.copy(
            wrongNumber = this.wrongNumber - 1,
        )
    }

    fun getProgress(totalNumber: Int): String {
        if(correctNumber + wrongNumber + 1 > totalNumber)
            return "$totalNumber / $totalNumber"
        return "${correctNumber + wrongNumber + 1} / ${totalNumber}"
    }
    fun getProgressInInfinityMode(): String
    {
        return "${correctNumber + wrongNumber} / \u221E"
    }
}

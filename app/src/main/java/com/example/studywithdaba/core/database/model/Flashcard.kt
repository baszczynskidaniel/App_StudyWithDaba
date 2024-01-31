package com.example.dabastudy.core.database.model.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity
data class Flashcard(

    val front: String,
    val back: String,
    val timestamp: Long = System.currentTimeMillis(),
    val frontStyle: String = "",

    val backStyle: String = "",

    val noteOriginId: Int? = null,
    val lastEdited: Long = timestamp,
    @PrimaryKey(autoGenerate = true)
    val flashcardId: Long = 0,
    val favourite: Boolean = false,
    val state: FlashcardStatus = FlashcardStatus.NEW,
    val nextRepetition: Long = timestamp,
    val level: Int = 0,
) {

    fun getIncrementedScore(): Int {
        return level + 1
    }

    fun getDecrementedScore(): Int {
        return if(level == 0) 0 else level - 1
    }

    fun getNextRepetitionTimeWithScoreChange(scoreChange: Int): Long {
        return System.currentTimeMillis() + getNextRepetitionDelayInMillis(level + scoreChange)
    }

    fun getNextRepetitionDelayInMillis(score: Int): Long {
        return when {
            score <= 0 -> 0
            score <= 1 -> TimeUnit.MINUTES.toMillis(1)
            score == 2 -> TimeUnit.DAYS.toMillis(1)
            score == 3 -> TimeUnit.DAYS.toMillis(6)
            score == 4 -> TimeUnit.DAYS.toMillis(7)
            score == 5 -> TimeUnit.DAYS.toMillis(16)
            score == 6 -> TimeUnit.DAYS.toMillis(33)
            score == 7 -> TimeUnit.DAYS.toMillis(84)
            score == 8 -> TimeUnit.DAYS.toMillis(210)
            else -> TimeUnit.DAYS.toMillis(365)
        }
    }
}



enum class FlashcardStatus {
    NEW,
    TO_REPEAT,
    REPEATED,
}

fun Flashcard.Reverse() = Flashcard(
    flashcardId = flashcardId,
    front = back,
    frontStyle = backStyle,
    back = front,
    backStyle = frontStyle,
    timestamp = timestamp,
)

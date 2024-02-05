package com.example.studywithdaba.feature_flashcard.quiz

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryEvent
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    var deckId: Long
    private val NUMBER_OF_ANSWER = 4

    private val _state = mutableStateOf(QuizState())
    val state: State<QuizState> = _state

    private var flashcards: List<Flashcard> = emptyList()
    init {
        deckId = savedStateHandle.get<Long>("deckId")!!
        viewModelScope.launch {
            flashcards = flashcardRepository.getFlashcardsInDeck(deckId).first()
            val generatedQuizQuestions = generateQuestionsForFront(flashcards)
            _state.value = _state.value.copy(
                enableCheck = true,
                quizQuestions = generatedQuizQuestions,
                currentQuestion = generatedQuizQuestions.first(),
                progressMessage = getProgressMessage(0, generatedQuizQuestions.size)
            )
        }
    }

    private fun getProgressMessage(currentPosition: Int, size: Int): String {
        return "${currentPosition + 1} / ${size}"
    }

    private fun generateQuestionsForFront(flashcards: List<Flashcard>): MutableList<QuizQuestion> {
        val shuffledFlashcards = flashcards.shuffled()
        val generatedQuestions: MutableList<QuizQuestion> = mutableListOf()
        for(flashcard in shuffledFlashcards) {
            val answers: MutableList<String> = mutableListOf(flashcard.back)
            var correctAnswerId: Int = -1
            var numberOfTriesToFindRandomFlashcard = 0
            for(i in 0 until NUMBER_OF_ANSWER - 1) {
                var randomFlashcard = shuffledFlashcards.random()

                while(
                    randomFlashcard.back in answers &&
                    numberOfTriesToFindRandomFlashcard < 10
                ) {
                    randomFlashcard = shuffledFlashcards.random()
                    numberOfTriesToFindRandomFlashcard++
                }
                if(numberOfTriesToFindRandomFlashcard < 10)
                    answers.add(randomFlashcard.back)


            }
            if(answers.size < 2)
                continue

            answers.shuffle()
            correctAnswerId = answers.indexOf(flashcard.back)
            generatedQuestions.add(
                QuizQuestion(
                    question = flashcard.front,
                    correctAnswerIndex = correctAnswerId,
                    answers = answers,
                    isQuestionForFront = true
                )
            )
        }
        return generatedQuestions
    }

    private fun generateQuestionsForBack(flashcards: List<Flashcard>): MutableList<QuizQuestion> {
        val shuffledFlashcards = flashcards.shuffled()
        val generatedQuestions: MutableList<QuizQuestion> = mutableListOf()
        for(flashcard in shuffledFlashcards) {
            val answers: MutableList<String> = mutableListOf(flashcard.front)
            var correctAnswerId: Int = -1
            for(i in 0 until NUMBER_OF_ANSWER - 1) {
                var randomFlashcard = shuffledFlashcards.random()
                var numberOfTriesToFindRandomFlashcard = 0
                while(
                    randomFlashcard.flashcardId == flashcard.flashcardId &&
                    randomFlashcard.front != flashcard.front &&
                    randomFlashcard.front !in answers &&
                    numberOfTriesToFindRandomFlashcard < 10
                ) {
                    randomFlashcard = shuffledFlashcards.random()
                    numberOfTriesToFindRandomFlashcard++
                }
                answers.add(randomFlashcard.front)

                answers.shuffle()
                correctAnswerId = answers.indexOf(flashcard.front)
            }
            if(answers.size < 2)
                continue
            generatedQuestions.add(
                QuizQuestion(
                    question = flashcard.back,
                    correctAnswerIndex = correctAnswerId,
                    answers = answers,
                    isQuestionForFront = false
                )
            )
        }
        return generatedQuestions
    }

    fun onEvent(event: QuizEvent) {
        when(event) {
            is QuizEvent.OnBack -> onBack(event.navController)
            is QuizEvent.OnClickAnswer -> onClickAnswer(event.answerIndex)
            QuizEvent.OnCheck -> onCheck()
            QuizEvent.OnDismissResult -> onDismissResult()
            is QuizEvent.OnSummaryDialogEvent -> onSummaryDialogEvent(event.event, event.navController)
        }
    }

    private fun onSummaryDialogEvent(event: StudySummaryEvent, navController: NavController) {
        when(event) {
            StudySummaryEvent.OnDismissRequest -> {
                navController.navigateUp()
            }
            StudySummaryEvent.OnDoneSummary -> {
                navController.navigateUp()
            }
            StudySummaryEvent.OnTryAgain -> {
                val generatedQuizQuestions = generateQuestionsForFront(flashcards)
                _state.value = _state.value.copy(
                    enableCheck = true,
                    quizQuestions = generatedQuizQuestions,
                    currentQuestion = generatedQuizQuestions.first(),
                    answeredQuestions = 0,
                    showSummary = false,
                    showResult = false,
                    currentPickedAnswer = -1,
                    isCurrentPickedAnswerCorrect = false,
                    checkContinueButtonName = "CHECK",
                    summary = StudySummaryState(),
                    progressMessage = getProgressMessage(0, generatedQuizQuestions.size)
                )
            }
        }
    }

    private fun onDismissResult() {
        if(_state.value.answeredQuestions + 1 >= _state.value.quizQuestions.size) {
            _state.value = _state.value.copy(
                enableCheck = false,
                showSummary = true,
                showResult = false,
                answeredQuestions = _state.value.answeredQuestions + 1,
                checkContinueButtonName = "CHECK"

            )
        } else {
            val nextQuestion = _state.value.quizQuestions[_state.value.answeredQuestions + 1]
            _state.value = _state.value.copy(
                enableCheck = true,
                showResult = false,
                answeredQuestions = _state.value.answeredQuestions + 1,
                currentQuestion = nextQuestion,
                currentPickedAnswer = -1,
                isCurrentPickedAnswerCorrect = false,
                progressMessage = getProgressMessage(_state.value.answeredQuestions + 1, _state.value.quizQuestions.size)
            )

        }
    }

    private fun onCheck() {
        _state.value = _state.value.copy(
            enableCheck = false,
            isCurrentPickedAnswerCorrect = _state.value.currentQuestion!!.isAnswerWithIndexCorrect(_state.value.currentPickedAnswer),
            showResult = true,
            checkContinueButtonName = "CONTINUE",
            summary = _state.value.summary.getSummaryWithNewAnswer(_state.value.currentQuestion!!.isAnswerWithIndexCorrect(_state.value.currentPickedAnswer))
        )


    }



    private fun onBack(navController: NavController) {
        navController.navigateUp()
    }

    private fun onClickAnswer(answerIndex: Int) {
        if(state.value.currentPickedAnswer == answerIndex) {
            _state.value = _state.value.copy(
                currentPickedAnswer = -1,
            )
        } else {
            _state.value = _state.value.copy(
                currentPickedAnswer = answerIndex
            )
        }
    }
}
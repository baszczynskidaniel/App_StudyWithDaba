package com.example.studywithdaba.Navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studywithdaba.feature.settings.SettingScreen
import com.example.studywithdaba.feature.settings.SettingsViewModel
import com.example.studywithdaba.feature_deck.add_deck.AddEditDeckScreen
import com.example.studywithdaba.feature_deck.add_deck.AddDeckViewModel
import com.example.studywithdaba.feature_deck.edit_deck.EditDeckScreen
import com.example.studywithdaba.feature_deck.edit_deck.EditDeckViewModel
import com.example.studywithdaba.feature_flashcard.RepeatScreen
import com.example.studywithdaba.feature_flashcard.add_flashcard.AddFlashcardScreen
import com.example.studywithdaba.feature_flashcard.add_flashcard.AddFlashcardViewModel
import com.example.studywithdaba.feature_flashcard.deck.DecksScreen
import com.example.studywithdaba.feature_flashcard.deck.DecksViewModel
import com.example.studywithdaba.feature_flashcard.edit_flashcard.EditFlashcardScreen
import com.example.studywithdaba.feature_flashcard.edit_flashcard.EditFlashcardViewModel
import com.example.studywithdaba.feature_flashcard.flashcards_in_deck.FlashcardsInDeckScreen
import com.example.studywithdaba.feature_flashcard.flashcards_in_deck.FlashcardsInDeckViewModel
import com.example.studywithdaba.feature_flashcard.flashcards_review.FlashcardsReviewScreen
import com.example.studywithdaba.feature_flashcard.quiz.QuizScreen
import com.example.studywithdaba.feature_home.HomeScreen
import com.example.studywithdaba.feature_note.NotesScreen
import com.example.studywithdaba.feature_note.NotesViewModel
import com.example.studywithdaba.feature_note.edit_note.EditNoteScreen

@Composable
fun NavigationGraph(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = Screen.Notes.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, innerPadding = innerPadding)
        }
        composable(
            Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                    animationSpec = tween(700)
                )
            }

        ) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            SettingScreen(
                onEvent = viewModel::onEvent,
                state = state.value,
                navController =  navController
            )
        }
        composable(
            Screen.FlashcardsReview.route + "?deckId={deckId}",
            arguments = listOf(
                navArgument(name = "deckId")
                {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )
        ) {
            FlashcardsReviewScreen(navController)
        }
        composable(
            Screen.EditFlashcard.route + "?flashcardId={flashcardId}",
            arguments = listOf(
                navArgument(name = "flashcardId")
                {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )
        ) {
            val viewModel: EditFlashcardViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            EditFlashcardScreen(
                navController = navController,
                state = state.value,
                onEvent = viewModel::onEvent
            )
        }


        composable(
            Screen.EditDeck.route + "?deckId={deckId}",
            arguments = listOf(
                navArgument(name = "deckId")
                {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )
        ) {
            val viewModel: EditDeckViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            EditDeckScreen(
                navController = navController,
                state = state.value,
                onEvent = viewModel::onEvent
            )
        }

        composable(
            Screen.Quiz.route + "?deckId={deckId}",
            arguments = listOf(
                navArgument(name = "deckId")
                {
                    type = NavType.LongType
                    defaultValue = -1
                }
            )
        ) {
            QuizScreen(navController)
        }


        composable(
            Screen.Notes.route,
        ) {
            val viewModel: NotesViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            NotesScreen(
                onEvent = viewModel::onEvent,
                state = state.value,
                navController =  navController,
                innerPaddingValues = innerPadding
            )
        }
        composable(
            Screen.Decks.route,
        ) {
            val viewModel: DecksViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            DecksScreen(
                onEvent = viewModel::onEvent,
                state = state.value,
                navController =  navController,
                innerPaddingValues = innerPadding
            )
        }

        composable(
            Screen.AddDeck.route,
        ) {
            val viewModel: AddDeckViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            AddEditDeckScreen(
                onEvent = viewModel::onEvent,
                state = state.value,
                navController =  navController,
            )
        }
        composable(
            Screen.FlashcardsInDeck.route + "?deckId={deckId}",

            arguments = listOf(
                navArgument(
                    name = "deckId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1

                }
            )
        ) {
            val viewModel: FlashcardsInDeckViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            FlashcardsInDeckScreen(
                onEvent = viewModel::onEvent,
                state = state.value,
                navController =  navController,
            )
        }

        composable(
            Screen.AddFlashcard.route + "?deckId={deckId}",

            arguments = listOf(
                navArgument(
                    name = "deckId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1

                }
            )
        ) {
            val viewModel: AddFlashcardViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            AddFlashcardScreen(
                onEvent = viewModel::onEvent,
                state = state.value,
                navController =  navController,
            )
        }
        composable(
            Screen.FlashcardsRepeat.route + "?deckId={deckId}",

            arguments = listOf(
                navArgument(
                    name = "deckId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1

                }
            )
        ) {

            RepeatScreen(
                navController =  navController,
            )
        }


        composable(
            Screen.EditNote.route + "?noteId={noteId}",

            arguments = listOf(
                navArgument(
                    name = "noteId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1

                }
            )
        ) {
            EditNoteScreen(
                navController =  navController,
            )
        }

    }
}
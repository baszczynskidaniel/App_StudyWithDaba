package com.example.studywithdaba.Navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studywithdaba.feature.settings.SettingScreen
import com.example.studywithdaba.feature.settings.SettingsEvent
import com.example.studywithdaba.feature.settings.SettingsScreen
import com.example.studywithdaba.feature.settings.SettingsState
import com.example.studywithdaba.feature.settings.SettingsViewModel
import com.example.studywithdaba.feature_home.HomeScreen
import com.example.studywithdaba.feature_note.NotesScreen
import com.example.studywithdaba.feature_note.NotesViewModel
import com.example.studywithdaba.feature_note.edit_note.EditNoteScreen
import com.example.studywithdaba.feature_note.edit_note.EditNoteViewModel

@Composable
fun NavigationGraph(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
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
            Screen.Notes.route,
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
            Screen.EditNote.route + "?noteId={noteId}",
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
            },
            arguments = listOf(
                navArgument(
                    name = "noteId"
                ) {
                    type = NavType.LongType
                    defaultValue = -1

                }
            )
        ) {

//            val viewModel: EditNoteViewModel = hiltViewModel()
//            val state = viewModel.state
            EditNoteScreen(
//                onEvent = viewModel::onEvent,
//                state = state.value,
                navController =  navController,

            )
        }
    }
}
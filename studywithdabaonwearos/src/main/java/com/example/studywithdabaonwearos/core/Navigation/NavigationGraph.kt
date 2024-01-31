package com.example.studywithdabaonwearos.core.Navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studywithdabaonwearos.feature_note.EditNoteScreen
import com.example.studywithdabaonwearos.feature_note.EditNoteViewModel
import com.example.studywithdabaonwearos.feature_note.NotesScreen
import com.example.studywithdabaonwearos.feature_note.NotesViewModel


@Composable
fun SWDNavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Notes.route) {
        composable(Screen.Notes.route) {
            val viewModel: NotesViewModel = hiltViewModel()
            val notes = viewModel.notes.collectAsState()
            NotesScreen(
                notes = notes.value,
                onAdd = viewModel::onAdd,
                navController = navController,
                onNoteClick = viewModel::onNoteClick
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
            val viewModel: EditNoteViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()
            EditNoteScreen(
                state = state.value,
                onEvent = viewModel::onEvent,
                navController =  navController,
            )
        }
    }

}
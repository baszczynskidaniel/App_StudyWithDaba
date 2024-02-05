package com.example.studywithdabaonwearos.feature_note

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.dabastudy.core.database.model.entities.Note
import com.example.studywithdaba.core.data.repository.NoteRepository
import com.example.studywithdabaonwearos.core.Navigation.Screen
import com.example.studywithdabaonwearos.presentation.theme.StudyWithDabaTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
): ViewModel() {
    private var _notes = noteRepository.getNotes().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList()
    )
    val notes = _notes

    fun onAdd(navController: NavController) {
        navController.navigate(Screen.EditNote.route)
    }
    fun onNoteClick(noteId: Long, navController: NavController) {
        navController.navigate(Screen.EditNote.route + "?noteId=${noteId}")
    }
}



@Composable
fun NotesScreen(
    notes: List<Note>,
    navController: NavController,
    onNoteClick: (Long, NavController) -> Unit,
    onAdd: (NavController) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CompactButton(onClick = { onAdd(navController) }) {
                androidx.wear.compose.material.Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
        items(notes.size) {index ->
            NoteItem(note = notes[index], onClick = {onNoteClick(notes[index].noteId, navController)})

        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: (Long) -> Unit,
) {
    Card(onClick = { onClick(note.noteId) }) {
        Column(
            modifier = Modifier.fillMaxWidth(),

        ) {
            Text(text = note.title, style = MaterialTheme.typography.title2, maxLines = 1, color = MaterialTheme.colors.primary)
            Text(text = note.content, style = MaterialTheme.typography.body2, maxLines = 4)
        }
    }
}


@Preview(
    widthDp = 213,
    heightDp = 213,
)
@Composable
internal fun NotesScreenPreview(

) {
    StudyWithDabaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colors.background)
        ) {
            NotesScreen(
                listOf(Note("Title", "content"),
                    Note("another title", "very long content")),
                navController = NavController(LocalContext.current),
                {_, _ ->},
                {  _ ->},
            )
        }
    }
}
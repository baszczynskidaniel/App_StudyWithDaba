package com.example.studywithdaba.feature_note.edit_note

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Note
import com.example.studywithdaba.core.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _state = mutableStateOf(EditNoteState())
    val state: State<EditNoteState> = _state
    private var noteId: Long

    init {
        Log.d("tu", "tutaj")
        noteId =  savedStateHandle.get<Long>("noteId") ?: -1L
        Log.d("tu", "po")
        if(noteId != -1L) {
            viewModelScope.launch {
                val note = noteRepository.getNoteById(noteId)!!
                _state.value = _state.value.copy(
                    title = note.title,
                    content = note.content,
                    date = note.lastEdited
                )
            }
        } else {
            viewModelScope.launch {
                noteId = noteRepository.insertNote(Note())
            }
        }
    }
    fun onEvent(event: EditNoteEvent) {
        when(event) {
            is EditNoteEvent.OnBack -> {
                if(state.value.content.isBlank() && state.value.title.isBlank())
                    viewModelScope.launch {
                        noteRepository.removeNoteById(noteId)
                    }
                event.navController.navigateUp()
            }

            is EditNoteEvent.OnContentChange -> {
                val timestamp = System.currentTimeMillis()
                _state.value = _state.value.copy(
                    content = event.contentChange,
                    date = timestamp
                )
                viewModelScope.launch {
                    noteRepository.updateNoteContentByNoteId(noteId, event.contentChange, timestamp)
                }
            }
            is EditNoteEvent.OnTitleChange -> {
                val timestamp = System.currentTimeMillis()
                _state.value = _state.value.copy(
                    title = event.titleChange,
                    date = timestamp
                )
                viewModelScope.launch {
                    noteRepository.updateNoteTitleByNoteId(noteId, event.titleChange, timestamp)
                }
            }
        }
    }

}

sealed class EditNoteEvent{
    data class OnTitleChange(val titleChange: String): EditNoteEvent()
    data class OnBack(val navController: NavController): EditNoteEvent()
    data class OnContentChange(val contentChange: String): EditNoteEvent()
}


data class EditNoteState(
    val title: String = "",
    val content: String = "",
    val date: Long = System.currentTimeMillis(),
)
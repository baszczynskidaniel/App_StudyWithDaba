package com.example.studywithdabaonwearos.feature_note

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import com.example.dabastudy.core.database.model.entities.Note
import com.example.studywithdaba.core.data.repository.NoteRepository
import com.example.studywithdabaonwearos.presentation.theme.StudyWithDabaTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private var noteId: Long
    private val _state = MutableStateFlow(EditNoteState())
    val state: StateFlow<EditNoteState> = _state
    private  var  textToSpeech: TextToSpeech? = null

    init {
        noteId = savedStateHandle.get<Long>("noteId") ?: -1L

        if(noteId == -1L) {
            viewModelScope.launch {
                noteId = noteRepository.insertNote(Note("", ""))
            }
        } else {
            viewModelScope.launch {
                val note = noteRepository.getNoteById(noteId)!!
                _state.update { it.copy(
                    title = note.title,
                    content = note.content
                    )
                }
            }
        }
    }
    fun onEvent(event: EditNoteEvent) {
        when(event) {
            is EditNoteEvent.OnConfirmDelete -> {
                viewModelScope.launch {
                    noteRepository.removeNoteById(noteId)
                    event.navController.navigateUp()
                }
            }
            is EditNoteEvent.OnContentChange -> {_state.update {
                it.copy(
                    content = event.contentChange
                )
            }
                viewModelScope.launch {
                    noteRepository.updateNoteContentByNoteId(noteId, event.contentChange, "", System.currentTimeMillis())
                }
            }

            EditNoteEvent.OnDismissDialog ->  {
            _state.update { it.copy(
                showDeleteDialog = false
            ) }
        }
            EditNoteEvent.OnRemove -> {
                _state.update { it.copy(
                    showDeleteDialog = true
                ) }
            }
            is EditNoteEvent.OnTitleChange -> {_state.update {
                it.copy(
                    title = event.titleChange,
                )
            }
                viewModelScope.launch {
                    noteRepository.updateNoteTitleByNoteId(noteId, event.titleChange, System.currentTimeMillis())
                }
            }
            is EditNoteEvent.OnVolume -> {
                _state.value = state.value.copy(
                    isTextToSpeechEnabled = false
                )
                textToSpeech = TextToSpeech(
                    event.context
                ) {
                    if (it == TextToSpeech.SUCCESS) {
                        textToSpeech?.let { txtToSpeech ->
                            txtToSpeech.language = Locale.US
                            txtToSpeech.setSpeechRate(1.0f)
                            txtToSpeech.speak(
                                _state.value.content,
                                TextToSpeech.QUEUE_ADD,
                                null,
                                null
                            )
                        }
                    }
                    _state.value = state.value.copy(
                        isTextToSpeechEnabled = true
                    )
                }
            }
        }
    }
}


data class EditNoteState(
    val title: String = "",
    val content: String = "",
    val showDeleteDialog: Boolean = false,
    val isTextToSpeechEnabled: Boolean = true,
)

sealed class EditNoteEvent{
    data class OnTitleChange(val titleChange: String): EditNoteEvent()
    data class OnContentChange(val contentChange: String): EditNoteEvent()
    object OnRemove: EditNoteEvent()
    data class OnVolume(val context: Context): EditNoteEvent()
    object OnDismissDialog: EditNoteEvent()
    data class OnConfirmDelete(val navController: NavController): EditNoteEvent()

}

@Composable
fun EditNoteScreen(
    state: EditNoteState,
    onEvent: (EditNoteEvent) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    EditNoteScreen(
        title = state.title,
        content = state.content,
        showDeleteDialog = state.showDeleteDialog,
        isTextToSpeechEnabled = state.isTextToSpeechEnabled,
        onTitleChange = { onEvent(EditNoteEvent.OnTitleChange(it))},
        onContentChange = { onEvent(EditNoteEvent.OnContentChange(it))},
        onRemove = { onEvent(EditNoteEvent.OnRemove)},
        onVolume = { onEvent(EditNoteEvent.OnVolume(context))},
        onDismissDialog = { onEvent(EditNoteEvent.OnDismissDialog)},
        onConfirmDelete = { onEvent(EditNoteEvent.OnConfirmDelete(navController))},
        navController =navController
    )
}

@Composable
fun EditNoteScreen(
    title: String,
    content: String,
    showDeleteDialog: Boolean,
    isTextToSpeechEnabled: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onRemove: () -> Unit,
    onVolume: () -> Unit,
    onDismissDialog: () -> Unit,
    onConfirmDelete: (NavController) -> Unit,
    navController: NavController,

) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                CompactButton(onClick = { onRemove() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                    )
                }
                CompactButton(onClick = { onVolume() }, enabled = isTextToSpeechEnabled) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                    )
                }
            }
        }
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { onTitleChange(it) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.title2.copy(
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                )
                if(title.isEmpty()) {
                    Text(
                        text = "Type title", style = MaterialTheme.typography.title2.copy(
                        color = MaterialTheme.colors.primary.copy(0.5f),
                        textAlign = TextAlign.Center
                    )
                    )
                }
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                BasicTextField(
                    modifier = Modifier.fillMaxSize(),
                    value = content,
                    onValueChange = {onContentChange(it)},
                    textStyle = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onBackground, textAlign = TextAlign.Justify)
                )
                if(content.isEmpty()) {
                    Text(
                        text = "your note ...",
                        style = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.onBackground.copy(0.5f),
                            textAlign = TextAlign.Justify)

                    )
                }
            }


        }
    }
    if(showDeleteDialog) {
        Alert(title = { Text("delete this note") }) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CompactButton(onClick = { onDismissDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    CompactButton(onClick = { onConfirmDelete(navController)}) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

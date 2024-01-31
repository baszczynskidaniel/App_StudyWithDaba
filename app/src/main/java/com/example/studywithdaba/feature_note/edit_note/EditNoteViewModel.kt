package com.example.studywithdaba.feature_note.edit_note

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Note
import com.example.richtextfield.RichTextString
import com.example.richtextfield.RichTextValue
import com.example.richtextfield.SpanStyleArgument
import com.example.studywithdaba.core.data.repository.NoteRepository
import com.example.studywithdaba.core.data.util.DefaultSpanStyle
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _state = mutableStateOf(EditNoteState())
    val state: State<EditNoteState> = _state
    private var noteId: Long
    private  var  textToSpeech: TextToSpeech? = null

    init {
        noteId =  savedStateHandle.get<Long>("noteId") ?: -1L

        if(noteId != -1L) {
            viewModelScope.launch {
                val note = noteRepository.getNoteById(noteId)!!

                val richTextValue: RichTextValue = RichTextValue(_state.value.style, note.contentStyle, text = note.content)
                richTextValue.text.value = TextFieldValue(note.content)

                _state.value = _state.value.copy(
                    title = note.title,
                    date = note.lastEdited,
                    richTextValue = richTextValue,
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
                if(state.value.richTextValue.text.value.text.isBlank() && state.value.title.isBlank())
                    viewModelScope.launch {
                        noteRepository.removeNoteById(noteId)
                    }
                event.navController.navigateUp()
            }

            is EditNoteEvent.OnContentChange -> {

                val timestamp = System.currentTimeMillis()
                _state.value = _state.value.copy(
                    date = timestamp
                )
                viewModelScope.launch {

                    _state.value.richTextValue.update(event.contentChange, _state.value.style)
                    _state.value = _state.value.copy(
                        style = _state.value.richTextValue.getSelectedStyle()

                    )
                    _state.value = _state.value.copy(
                        isGreen = _state.value.style.color == Color(0xFF00FF00).copy(0.99f)

                    )
                    val dataCLassList = _state.value.richTextValue.toDataClassList()
                    val json = RichTextString.toJsonString(dataCLassList)
                    noteRepository.updateNoteContentByNoteId(noteId, event.contentChange.text, json, timestamp)
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

            is EditNoteEvent.OnShowColorPickerChange -> {

                _state.value = _state.value.copy(
                    showColorPicker = event.change
                )
            }
            is EditNoteEvent.OnStyleChange -> {
                _state.value.richTextValue.updateSelectionWithStyle(event.styleChange)
                _state.value = _state.value.copy(
                    style = event.styleChange
                )
            }

            is EditNoteEvent.OnTextToSpeech -> {
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
                                _state.value.richTextValue.text.value.text,
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

sealed class EditNoteEvent{
    data class OnTextToSpeech(val context: Context): EditNoteEvent()
    data class OnTitleChange(val titleChange: String): EditNoteEvent()
    data class OnBack(val navController: NavController): EditNoteEvent()
    data class OnContentChange(val contentChange: TextFieldValue): EditNoteEvent()

    data class OnStyleChange(val styleChange: SpanStyle): EditNoteEvent()
    data class OnShowColorPickerChange(val change: Boolean): EditNoteEvent()
}


data class EditNoteState(
    val isTextToSpeechEnabled: Boolean = true,
    val showColorPicker: Boolean = false,
    val isGreen: Boolean = false,
    val style: SpanStyle = DefaultSpanStyle.spanStyle,
    val richTextValue: RichTextValue = RichTextValue(style),
    val title: String = "",
    val date: Long = System.currentTimeMillis(),
)

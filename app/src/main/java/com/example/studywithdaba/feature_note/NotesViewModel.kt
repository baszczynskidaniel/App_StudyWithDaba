package com.example.studywithdaba.feature_note

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.dabastudy.core.database.model.entities.Note
import com.example.dabastudy.core.database.model.entities.SearchHistory
import com.example.dabastudy.core.database.model.entities.SearchScope
import com.example.dabastudy.core.database.model.entities.relations.DeckFlashcardCrossRef
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.repository.FlashcardRepository
import com.example.studywithdaba.core.data.repository.NoteRepository
import com.example.studywithdaba.core.data.repository.SearchHistoryRepository
import com.example.studywithdaba.core.design_system.component.SWDSearchBarEvent
import com.example.studywithdaba.core.design_system.component.SWDSearchBarState
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// TODO save grid in preferences

data class FilterState(
    val sortBy: NotesSortBy = NotesSortBy.DEFAULT,
    val filterBy: NotesFilterBy = NotesFilterBy.DEFAULT,
    val invertOrder: Boolean = false,
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository
): ViewModel() {
    private val _searchHistory = searchHistoryRepository.getSearchHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _events = Channel<NotesViewModel.ValidationEvent>()
    val event = _events.receiveAsFlow()

    sealed class ValidationEvent {
        data class Success(val message: String): ValidationEvent()
        data class Error(val message: String): ValidationEvent()
    }

    private val _searchState = MutableStateFlow(SWDSearchBarState())
    private val searchState = combine(_searchState, _searchHistory) {
        searchState, searchHistory ->
            searchState.copy(history = searchHistory.map { it.query })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SWDSearchBarState())

    private val _state = MutableStateFlow(NotesState())
    private val _notes = noteRepository.getNotes().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    private val _filterState = MutableStateFlow(FilterState())
    
    val state = combine(_state, _notes, _filterState, searchState) {
        state, notes, filterState, searchState -> state.copy(
            notes = getArrangedNotes(),
            searchState = searchState,
            areNotesInInvertedOrder = filterState.invertOrder,
            sortBy = filterState.sortBy,
            filterBy = filterState.filterBy,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesState())

    fun onEvent(event: NotesEvent) {
        when (event) {

            NotesEvent.OnClearSelection -> {
                _state.update {
                    it.copy(
                        selectedNotesId = emptySet()
                    )
                }
            }

            is NotesEvent.OnFilterByDismiss -> {
                if (event.filterByChange != null)
                    _filterState.update {
                        it.copy(
                            filterBy = event.filterByChange
                        )
                    }
                _state.update {
                    it.copy(
                        showFilterByDialog = false
                    )
                }
            }

            NotesEvent.OnFilterByClick -> {
                _state.update {
                    it.copy(
                        showFilterByDialog = true
                    )
                }
            }

            is NotesEvent.OnGridSizeChange -> {
                _state.value = _state.value.copy(
                    gridSize = if (event.oldValue == 1) 2 else 1
                )
            }

            is NotesEvent.OnInvert -> {
                _filterState.update {
                    it.copy(
                        invertOrder = !it.invertOrder
                    )
                }
            }

            is NotesEvent.OnNoteItemClick -> {
                event.navController.navigate(Screen.EditNote.route + "?noteId=${event.noteId}")
            }

            is NotesEvent.OnNoteItemFavouriteClick -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        noteRepository.updateFavouriteByNoteId(event.noteId, event.favouriteChange)
                    }
                }
            }

            is NotesEvent.OnNoteItemLongClick -> {
                val newSelectedNotes = _state.value.selectedNotesId.toMutableSet()
                if (event.noteId in _state.value.selectedNotesId)
                    newSelectedNotes.remove(event.noteId)
                else
                    newSelectedNotes.add(event.noteId)
                _state.update {
                    it.copy(
                        selectedNotesId = newSelectedNotes
                    )
                }
            }

            NotesEvent.OnRemoveSelectedNotes -> {
                viewModelScope.launch {
                    noteRepository.removeNoteByNoteIds(_state.value.selectedNotesId.toList())
                    _state.update {
                        it.copy(
                            selectedNotesId = emptySet()
                        )
                    }
                }
            }

            NotesEvent.OnReset -> {
                _filterState.update {
                    it.copy(
                        sortBy = NotesSortBy.DEFAULT,
                        filterBy = NotesFilterBy.DEFAULT,
                        invertOrder = false,
                    )
                }
            }

            is NotesEvent.OnSearchEvent -> {
                onSearchEvent(event.event)
            }

            is NotesEvent.OnSelectionFavouriteChange -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        noteRepository.updateFavouriteForNoteIds(
                            _state.value.selectedNotesId.toList(),
                            event.oldValue
                        )
                    }
                }
            }

            NotesEvent.OnSettings -> {
                TODO()
            }

            is NotesEvent.OnSortByDismiss -> {
                if (event.sortByChange != null)
                    _filterState.update {
                        it.copy(
                            sortBy = event.sortByChange
                        )
                    }
                _state.update {
                    it.copy(
                        showSortByDialog = false
                    )
                }
            }

            NotesEvent.OnSortByClick -> {
                _state.update {
                    it.copy(
                        showSortByDialog = true
                    )
                }
            }

            is NotesEvent.OnAddNote -> {
                event.navController.navigate(Screen.EditNote.route)
            }

            is NotesEvent.OnMoreNoteClick -> {
                _state.update {
                    it.copy(
                        showNoteBottomSheet = true,
                        bottomSheetNoteId = event.noteId
                    )
                }
            }

            is NotesEvent.OnNoteBottomSheetEvent -> {
                when (event.event) {
                    NoteBottomSheetEvent.OnDismiss -> {
                        _state.update {
                            it.copy(
                                showNoteBottomSheet = false,
                                bottomSheetNoteId = -1L,
                            )
                        }
                    }

                    is NoteBottomSheetEvent.OnGenerateFlashcards -> {

                        generateFlashcardsFromNoteWithAi(event.event.noteId)
                    }

                    is NoteBottomSheetEvent.OnRemove -> {
                        viewModelScope.launch {
                            noteRepository.removeNoteById(event.event.noteId)
                        }
                        val selectedNotes = _state.value.selectedNotesId.toMutableSet()
                        selectedNotes.remove(event.event.noteId)
                        _state.update {
                            it.copy(
                                showNoteBottomSheet = false,
                                bottomSheetNoteId = -1L,
                                selectedNotesId = selectedNotes
                            )
                        }
                        viewModelScope.launch {
                            _events.send(ValidationEvent.Success("note was removed"))
                        }
                    }
                }
            }
        }
    }



    private fun insertFlashcards(flashcards: List<Flashcard>) {
        viewModelScope.launch {
            val deckId = deckRepository.insertDeck(Deck("Generated by Ai"))
            flashcards.forEach {
                val flashcardId = flashcardRepository.insertFlashcard(it)
                flashcardRepository.insertDeckFlashcardCrossRef(DeckFlashcardCrossRef(deckId, flashcardId))
            }
        }
    }

    private fun generateFlashcardsFromNoteWithAi(noteId: Long) {
        var body: String? = ""
        val note = _notes.value.filter { it.noteId == noteId }.first()
        var flashcards = listOf<Flashcard>()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val apiKey = "API_KEY"
        val url = "https://api.openai.com/v1/chat/completions"


        val requestBody = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [
            {"role": "system", "content": "generate questions with answers for text in format: question;answer;question; ..."},
            {"role": "user", "content": "${note.content}"}
            ],
            "max_tokens": 1000
        }
    """.trimIndent()

        try {


            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("error", "API Failed", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body1 = response.body?.string()

                    if (body1 != null) {
                        Log.v("reponse", body1)
                        body = body1
                        val jsonObject = JsonParser.parseString(body).asJsonObject
                        val content = jsonObject.getAsJsonArray("choices")[0].asJsonObject
                            .getAsJsonObject("message")
                            .getAsJsonPrimitive("content").asString

                        flashcards = parseContentToFlashcards(content)
                        insertFlashcards(flashcards)
                        Log.v("flashcards", flashcards.toString())

                    } else {
                        Log.v("empty", "empty")
                    }
                }
            })
        } catch (e: Exception) {
            Log.v("error", e.message.toString())
        }
    }

    private fun parseContentToFlashcards(response: String): List<Flashcard> {
        val flashcards = mutableListOf<Flashcard>()
        val sections = response.split("\n\n")
        for(section in sections) {
            val parts = section.split("\n")
            if(parts.size >= 2) {
                val definition = parts[0].substringAfter(". ")
                val answer = parts.subList(1, parts.size).joinToString("\n")
                if(definition.isNotBlank() && answer.isNotBlank())
                    flashcards.add(Flashcard(definition, answer))
            }
        }
        return flashcards
    }

    private fun onSearchEvent(event: SWDSearchBarEvent) {
        when(event) {
            is SWDSearchBarEvent.OnActiveChange -> {
                _searchState.update {   it.copy(
                    isActive = !event.activeChange
                    )
                }
            }
            SWDSearchBarEvent.OnBack ->  {
                _searchState.update {   it.copy(
                    isActive = false,
                    query = ""
                    )
                }
            }
            SWDSearchBarEvent.OnHistoryClear -> {
                viewModelScope.launch {
                    searchHistoryRepository.removeSearchHistoryInScope(SearchScope.NOTE_ONLY)
                }
            }
            is SWDSearchBarEvent.OnHistoryClick -> {
                _searchState.update {   it.copy(
                    isActive = false,
                    query = event.historyQuery
                    )
                }
                viewModelScope.launch {
                    searchHistoryRepository.updateSearchHistoryById(_searchHistory.value[event.historyIndex].searchHistoryId)
                }
            }
            is SWDSearchBarEvent.OnQueryChange -> {
                _searchState.update {   it.copy(
                    query = event.queryChange
                    )
                }
            }
            SWDSearchBarEvent.OnQueryClear -> {
                _searchState.update {   it.copy(
                    query = ""
                )
                }
            }
            is SWDSearchBarEvent.OnRemoveHistory -> {
                viewModelScope.launch {
                    searchHistoryRepository.removeSearchHistoryById(_searchHistory.value[event.historyIndex].searchHistoryId)
                }
            }
            is SWDSearchBarEvent.OnSearch -> {
                _searchState.update {   it.copy(
                    isActive = false,
                    )
                }
                if(event.query !in _searchState.value.history)
                    viewModelScope.launch {
                        searchHistoryRepository.insertSearchHistory(SearchHistory(
                            query = event.query,
                            searchScope = SearchScope.NOTE_ONLY
                        )
                    )
                }
            }
        }
    }

    private fun getArrangedNotes(): List<Note> {
        var arrangedNotes = _notes.value.toMutableList()
        if(_filterState.value.filterBy == NotesFilterBy.ONLY_FAVOURITE)
            arrangedNotes.removeAll { !it.favourite }
        if(_searchState.value.query.length > 1) {
            arrangedNotes = arrangedNotes.filter { doesSearchMatch(it) }.toMutableList()
        }
        when(_filterState.value.sortBy) {
            NotesSortBy.DEFAULT -> arrangedNotes.sortBy { it.lastEdited }
            NotesSortBy.DATE -> arrangedNotes.sortBy { it.lastEdited }
            NotesSortBy.TITLE -> arrangedNotes.sortBy { it.title }
            NotesSortBy.CONTENT -> arrangedNotes.sortBy { it.content }
        }
        if(!_filterState.value.invertOrder)
            arrangedNotes.reverse()

        return arrangedNotes
    }

    private fun doesSearchMatch(note: Note): Boolean {
        return (note.content.contains(_searchState.value.query, true) || note.title.contains(_searchState.value.query, true))
    }
}
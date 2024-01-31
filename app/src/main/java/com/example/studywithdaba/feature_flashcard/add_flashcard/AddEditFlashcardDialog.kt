package com.example.studywithdaba.feature_flashcard.add_flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


data class AddEditFlashcardState(
    val autoMove: Boolean = true,
    val label: String = "",
    val definitionError: String? = null,
    val definition: String = "",
    val answer: String = "",
    val answerError: String? = null,
    val deckTitlesWithIds: List<DeckTitleWithDeckId> = emptyList(),
    val selectedDecks: Set<DeckTitleWithDeckId> = emptySet(),
    val showDecks: Boolean = false,
    val decksError: String? = null,
)

sealed class AddEditFlashcardEvent {
    data class OnAutoMoveChange(val valueChange: Boolean): AddEditFlashcardEvent()
    data class OnDeckClick(val deckId: Long): AddEditFlashcardEvent()
    object OnApply: AddEditFlashcardEvent()
    object OnDefinitionClear: AddEditFlashcardEvent()
    object OnAnswerClear: AddEditFlashcardEvent()
    object OnSwap: AddEditFlashcardEvent()
    data class OnAnswerChange(val answerChange: String): AddEditFlashcardEvent()
    data class OnDefinitionChange(val definitionChange: String): AddEditFlashcardEvent()
    data class OnDeckSelection(val deck: DeckTitleWithDeckId, val selectedChange: Boolean): AddEditFlashcardEvent()
    data class OnShowDecks(val showChange: Boolean): AddEditFlashcardEvent()
}

data class DeckTitleWithDeckId(
    val deckId: Long,
    val title: String,
) {
    companion object {
        fun getFromDeck(deck: Deck): DeckTitleWithDeckId {
            return DeckTitleWithDeckId(deck.deckId, deck.title)
        }

        fun getListFromDecks(decks: List<Deck>): List<DeckTitleWithDeckId> {
            return decks.map { DeckTitleWithDeckId(it.deckId, it.title) }
        }

        fun toDeckIdsSet(deckTitlesWithDeckIds: List<DeckTitleWithDeckId>): Set<Long> {
            return deckTitlesWithDeckIds.map { it.deckId }.toSet()
        }
    }
}


@Composable
fun AddEditFlashcardDialog(
    state: AddEditFlashcardState,
    onEvent: (AddEditFlashcardEvent) -> Unit,
    onDismiss: () -> Unit
) {
    AddEditFlashcardDialog(
        autoMove = state.autoMove,
        onAutoMoveChange = { onEvent(AddEditFlashcardEvent.OnAutoMoveChange(it))},
        onDeckClick = { onEvent(AddEditFlashcardEvent.OnDeckClick(it))},
        label = state.label,
        onDismiss = { onDismiss()},
        onApply = { onEvent(AddEditFlashcardEvent.OnApply)},
        definitionError = state.definitionError,
        definition = state.definition,
        onDefinitionClear = { onEvent(AddEditFlashcardEvent.OnDefinitionClear)},
        onDefinitionChange = { onEvent(AddEditFlashcardEvent.OnDefinitionChange(it))},
        answerError = state.answerError,
        answer = state.answer,
        onAnswerClear = { onEvent(AddEditFlashcardEvent.OnAnswerClear)},
        onAnswerChange = { onEvent(AddEditFlashcardEvent.OnAnswerChange(it))},
        onSwap = { onEvent(AddEditFlashcardEvent.OnSwap)},
        onDeckSelection =  { deck, selectionChange -> onEvent(AddEditFlashcardEvent.OnDeckSelection(deck, selectionChange)) },
        deckTitlesWithIds = state.deckTitlesWithIds,
        showDeckSelection = state.showDecks,
        onShowDecks = { onEvent(AddEditFlashcardEvent.OnShowDecks(it))},
        selectedDeckIds = state.selectedDecks,
        decksError = state.decksError,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AddEditFlashcardDialog(
    autoMove: Boolean,
    onAutoMoveChange: (Boolean) -> Unit,
    onDeckClick: (Long) -> Unit,
    label: String,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    definitionError: String?,
    definition: String,
    onDefinitionClear: () -> Unit,
    onDefinitionChange: (String) -> Unit,
    answerError: String?,
    answer: String,
    onAnswerClear: () -> Unit,
    onAnswerChange: (String) -> Unit,
    onSwap: () -> Unit,
    onDeckSelection: (DeckTitleWithDeckId, Boolean) -> Unit,
    onShowDecks: (Boolean) -> Unit,
    selectedDeckIds: Set<DeckTitleWithDeckId>,
    deckTitlesWithIds: List<DeckTitleWithDeckId>,
    showDeckSelection: Boolean,
    decksError: String?
) {
    val focusManager = LocalFocusManager.current
    val (textField1, textField2) = remember { FocusRequester.createRefs() }
    val keyboardController = LocalSoftwareKeyboardController.current
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
    ) {

        Card(
            modifier = Modifier
                .width(LocalDimensions.current.alertDialogWidth)
                .padding(LocalDimensions.current.doubleDefaultPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalDimensions.current.defaultPadding),
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Column(
                    modifier = Modifier
                        .weight(1f, false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Divider(thickness = LocalDimensions.current.dividerThickness, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = autoMove,
                            onCheckedChange = { onAutoMoveChange(!autoMove) })
                        Text(text = "confirm button on keyboard move to next text field", style = MaterialTheme.typography.labelLarge)
                    }
                    Divider(thickness = LocalDimensions.current.dividerThickness, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    if(decksError != null) {
                        Text(
                            text = decksError,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Selected decks: ${deckTitlesWithIds.size}")
                        TextButton(onClick = { onShowDecks(!showDeckSelection) }) {
                            Text(text = if(showDeckSelection) "hide decks " else "show decks ")
                            Icon(imageVector = if(showDeckSelection) SWDIcons.Hide else SWDIcons.Expand, contentDescription = null)
                        }
                    }

                    if(selectedDeckIds.isNotEmpty()) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding)
                        ) {
                            for (deck in deckTitlesWithIds) {
                                InputChip(
                                    selected = false,
                                    onClick = { onDeckClick(deck.deckId) },
                                    label = { Text(text = deck.title) },
                                    trailingIcon = { Icon(SWDIcons.Clear, null) }
                                )
                            }
                        }
                    }
                    Divider(thickness = LocalDimensions.current.dividerThickness, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    if(deckTitlesWithIds.isNotEmpty() && showDeckSelection) {
                        FlowRow(
                            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding),
                            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding)
                        ) {
                            for(deck in deckTitlesWithIds) {
                                val selected = deck in selectedDeckIds
                                FilterChip(
                                    selected = selected,
                                    onClick = { onDeckSelection(deck, !selected) },
                                    label = { deck.title },
                                    trailingIcon = {
                                        if(selected)
                                            Icon(SWDIcons.Check, null)
                                    }
                                )
                            }
                        }
                        Divider(thickness = LocalDimensions.current.dividerThickness, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    }

                    FilledIconButton(onClick = { onSwap() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Icon(SWDIcons.SwapVertically, null)
                    }
                    Divider(thickness = LocalDimensions.current.dividerThickness, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                    TextField(
                        maxLines = 7,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(textField1),

                        placeholder = {
                            Text("Definition")
                        },
                        isError = definitionError != null,
                        supportingText = {
                            if (definitionError != null)
                                Text(text = definitionError)
                            else
                                Text("*required")
                        },
                        value = definition,
                        onValueChange = { onDefinitionChange(it) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if(autoMove) ImeAction.Next else ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                textField2.requestFocus()
                            }
                        ),
                        trailingIcon = {
                            if (definition.isNotBlank()) {
                                IconButton(onClick = { onDefinitionClear() }) {
                                    Icon(SWDIcons.Clear, null)
                                }
                            }
                        },
                        label = {
                            Text(text = "Definition")
                        },

                    )

                    TextField(
                        maxLines = 7,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(textField2),
                        placeholder = {
                            Text("Answer")
                        },
                        isError = answerError != null,
                        supportingText = {
                            if (answerError != null)
                                Text(text = answerError)
                            else
                                Text("*required")
                        },
                        value = answer,
                        onValueChange = { onAnswerChange(it) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if(autoMove) ImeAction.Done else ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onApply()
                                if(answerError == null && definitionError == null && autoMove)
                                    textField1.requestFocus()
                            }
                        ),
                        trailingIcon = {
                            if (answer.isNotBlank()) {
                                IconButton(onClick = { onAnswerClear() }) {
                                    Icon(SWDIcons.Clear, null)
                                }
                            }
                        },
                        label = {
                            Text(text = "Answer")
                        }
                    )
                }
                Divider(thickness = LocalDimensions.current.dividerThickness, color = MaterialTheme.colorScheme.onBackground.copy(0.5f))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = { onApply() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(text = "Apply")
                    }
                }
            }
        }
    }
}


@Preview
@Composable
internal fun AddEditFlashcardPreview(
) {
    val _state = MutableStateFlow(AddEditFlashcardState(
        label = "Preview",
        deckTitlesWithIds = listOf(
            DeckTitleWithDeckId(1, "first"),
            DeckTitleWithDeckId(2, "second"),
            DeckTitleWithDeckId(3, "third"),
        )
    ))

    StudyWithDabaTheme(
        darkTheme = true
    ) {

        val state = _state.collectAsState()
        var showDialog by remember {
            mutableStateOf(false)
        }
        fun onEvent(event: AddEditFlashcardEvent) {
            when(event) {
                is AddEditFlashcardEvent.OnAnswerChange -> _state.update { it.copy(answer = event.answerChange) }
                AddEditFlashcardEvent.OnAnswerClear -> _state.update { it.copy(answer = "") }
                AddEditFlashcardEvent.OnApply -> _state.update { it.copy(answer = "", definition = "") }
                is AddEditFlashcardEvent.OnAutoMoveChange -> _state.update { it.copy(autoMove = event.valueChange) }
                is AddEditFlashcardEvent.OnDeckClick -> TODO()
                is AddEditFlashcardEvent.OnDeckSelection -> TODO()
                is AddEditFlashcardEvent.OnDefinitionChange -> _state.update { it.copy(definition = event.definitionChange) }
                AddEditFlashcardEvent.OnDefinitionClear -> _state.update { it.copy(definition = "") }
                AddEditFlashcardEvent.OnSwap -> _state.update { it.copy(answer = it.definition, definition = it.answer) }
                is AddEditFlashcardEvent.OnShowDecks -> TODO()
                else -> {}
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Button(onClick = { showDialog = true }) {
                Text(text = "Show dialog")
            }
            AddEditFlashcardDialog(state = state.value, onEvent = { onEvent(it)}, onDismiss = {showDialog = false})
        }
    }
}



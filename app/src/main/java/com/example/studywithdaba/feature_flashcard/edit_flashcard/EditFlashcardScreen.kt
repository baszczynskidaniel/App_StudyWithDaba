package com.example.studywithdaba.feature_flashcard.edit_flashcard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.studywithdaba.core.design_system.component.ErrorRowMessage
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.feature_flashcard.add_flashcard.AddFlashcardState

@Composable
fun EditFlashcardScreen(
    state: EditFlashcardState,
    onEvent: (EditFlashcardEvent) -> Unit,
    navController: NavController,
) {
    EditFlashcardScreen(
        definition = state.definition,
        answer = state.answer,
        definitionError = state.definitionError,
        answerError = state.answerError,
        autoMove = state.autoMove,
        showAllDecks = state.showAllDecks,
        decks = state.decks,
        selectedDecks = state.selectedDecks,
        deckError = state.deckError,
        favourite = state.favourite,
        onBack = { onEvent(EditFlashcardEvent.OnBack(navController)) },
        onFavouriteChange = { onEvent(EditFlashcardEvent.OnFavouriteChange(it)) },
        onDefinitionChange = { onEvent(EditFlashcardEvent.OnDefinitionChange(it)) },
        onDefinitionClear = { onEvent(EditFlashcardEvent.OnDefinitionClear) },
        onAnswerChange = { onEvent(EditFlashcardEvent.OnAnswerChange(it)) },
        onAnswerClear = { onEvent(EditFlashcardEvent.OnAnswerClear) },
        onShowAllDecksChange = { onEvent(EditFlashcardEvent.OnShowAllDecksChange(it)) },
        onInputDeckClick = { onEvent(EditFlashcardEvent.OnInputDeckClick(it)) },
        onFilterDeckClick = { deck, selectedChange -> onEvent(EditFlashcardEvent.OnFilterDeckClick(deck, selectedChange)) },
        onSwap = { onEvent(EditFlashcardEvent.OnSwap) },
        onAutoMoveChange = { onEvent(EditFlashcardEvent.OnAutoMoveChange(it)) },
        onApplyChanges = { onEvent(EditFlashcardEvent.OnApplyChanges(navController))}
    )
}


@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun EditFlashcardScreen(
    definition: String,
    answer: String,
    definitionError: String?,
    answerError: String?,
    autoMove: Boolean,
    showAllDecks: Boolean,
    decks: List<Deck>,
    selectedDecks: Set<Deck>,
    deckError: String?,
    favourite: Boolean,

    onBack: () -> Unit,
    onFavouriteChange: (Boolean) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onDefinitionClear: () -> Unit,
    onAnswerChange: (String) -> Unit,
    onAnswerClear: () -> Unit,
    onShowAllDecksChange: (Boolean) -> Unit,
    onInputDeckClick: (Deck) -> Unit,
    onFilterDeckClick: (Deck, Boolean) -> Unit,
    onSwap: () -> Unit,
    onAutoMoveChange: (Boolean) -> Unit,
    onApplyChanges: () -> Unit
) {
    val (answerFocus, definitionFocus) = remember {
        FocusRequester.createRefs()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = LocalDimensions.current.defaultPadding,
                bottom = LocalDimensions.current.defaultPadding,
                end = LocalDimensions.current.defaultPadding,
            )
            .navigationBarsPadding()
        ,

        ) {
        CenterAlignedTopAppBar(
            title = { Text(text = "Edit flashcard") },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(imageVector = SWDIcons.Back, contentDescription = null)
                }
            },
            actions = {
                IconButton(onClick = { onFavouriteChange(!favourite) }) {
                    if(favourite)
                        Icon(SWDIcons.FavouriteFilled, null, tint = MaterialTheme.colorScheme.primary)
                    else
                        Icon(SWDIcons.FavouriteOutlined, null)
                }
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding)
        ) {
            Divider(thickness = LocalDimensions.current.dividerThickness)
            Text(
                text = "Selected decks",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if(selectedDecks.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding)
                ) {
                    selectedDecks.forEach { deck ->
                        InputChip(
                            selected = true,
                            onClick = { onInputDeckClick(deck) },
                            label = { Text(text = deck.title) },
                            trailingIcon = {
                                Icon(SWDIcons.Clear, null)
                            }
                        )
                    }
                }
            }
            Divider(thickness = LocalDimensions.current.dividerThickness)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("All decks", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium,)
                TextButton(onClick = { onShowAllDecksChange(!showAllDecks) }) {
                    Text(text = if(showAllDecks) "Hide " else "Expand ")
                    Icon(imageVector = if(showAllDecks) SWDIcons.Hide else SWDIcons.Expand, contentDescription = null)
                }
            }
            AnimatedVisibility(visible = showAllDecks) {
                if(decks.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding)
                    ) {
                        decks.forEach { deck ->
                            FilterChip(
                                selected = deck in selectedDecks,
                                onClick = { onFilterDeckClick(deck, (deck in selectedDecks)) },
                                label = { Text(text = deck.title) },
                                trailingIcon = {
                                    if(deck in selectedDecks)
                                        Icon(SWDIcons.Check, null)
                                }
                            )
                        }
                    }
                } else {
                    Text(text = "There are not any deck")
                }
            }
            Divider(thickness = LocalDimensions.current.dividerThickness)
            Text(
                text = "Flashcard",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(definitionFocus),
                trailingIcon = {
                    if(definition.isNotBlank()) {
                        IconButton(onClick = { onDefinitionClear() }) {
                            Icon(SWDIcons.Clear, null)
                        }
                    }
                },
                isError = definitionError != null,
                supportingText = {
                    if(definitionError != null)
                        Text(text = definitionError)
                    else
                        Text("*required")
                },
                placeholder = {
                    Text("Definition")
                },
                value = definition,
                onValueChange = { onDefinitionChange(it)  },
                label = { Text("Definition") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = if(autoMove) ImeAction.Next else ImeAction.Default
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        answerFocus.requestFocus()
                    }
                ),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { onSwap() }) {
                    Icon(
                        SWDIcons.SwapVertically, null,
                        tint = MaterialTheme.colorScheme.primary,

                        )
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().focusRequester(answerFocus),
                trailingIcon = {
                    if(answer.isNotBlank()) {
                        IconButton(onClick = { onAnswerClear() }) {
                            Icon(SWDIcons.Clear, null)
                        }
                    }
                },
                isError = answerError != null,
                supportingText = {
                    if(answerError != null)
                        Text(text = answerError)
                    else
                        Text("*required")
                },
                placeholder = {
                    Text("Answer")
                },
                value = answer,
                onValueChange = {  onAnswerChange(it)},
                label = { Text("Answer") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = if(autoMove) ImeAction.Done else ImeAction.Default
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        definitionFocus.requestFocus()
                        onApplyChanges()


                    }
                ),
            )
            Divider(thickness = LocalDimensions.current.dividerThickness)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = autoMove, onCheckedChange = { onAutoMoveChange(!autoMove)})
                Text(text = "Confirm button on keyboard moves to another field", color = MaterialTheme.colorScheme.onBackground)
            }
        }
        if(definitionError != null) {
            ErrorRowMessage(definitionError)
        }
        if(answerError != null) {
            ErrorRowMessage(answerError)
        }
        if(deckError != null) {
            ErrorRowMessage(deckError)
        }
        Button(onClick = { onApplyChanges() }, modifier = Modifier.fillMaxWidth() ) {
            Text(text = "Apply changes")
        }
    }
}


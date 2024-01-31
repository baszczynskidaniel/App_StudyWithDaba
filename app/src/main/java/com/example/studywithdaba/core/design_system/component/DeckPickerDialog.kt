package com.example.studywithdaba.core.design_system.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DeckPickerDialog(
    decks: List<Deck>,
    selectedDeckIds: Set<Long>,
    onDismiss: (Set<Long>) -> Unit,
    onDeckClick: (Long) -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismiss(selectedDeckIds) },
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
                    text = "Select decks",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                    ) {
                        for (deck in decks.sortedBy { it.title }) {
                            val selected = deck.deckId in selectedDeckIds
                            FilterChip(
                                selected = selected,
                                onClick = { onDeckClick(deck.deckId) },
                                label = {
                                    Text(text = deck.title)
                                },
                                leadingIcon = {
                                    if (selected) {
                                        Icon(SWDIcons.Check, null)
                                    }
                                },
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss(selectedDeckIds) }) {
                        Text(text = "Done", )
                    }
                }
            }
        }
    }
}
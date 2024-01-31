package com.example.studywithdaba.feature_flashcard.study_summary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.listItemModifier

@Composable
fun StudySummaryDialog(
    onEvent: (StudySummaryEvent) -> Unit,
    state: StudySummaryState,
    label: String,
    isAgainOptionAvailable: Boolean
) {
    Dialog(
        onDismissRequest = {
            onEvent(StudySummaryEvent.OnDismissRequest)
        },
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
                    .verticalScroll(rememberScrollState())
                    .padding(LocalDimensions.current.defaultPadding)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = listItemModifier(),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = {
                        onEvent(StudySummaryEvent.OnDismissRequest)
                    }) {
                        Icon(SWDIcons.Clear, null)
                    }
                }
                Text(
                    text = state.getScoreMessage(),
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.labelLarge

                )
                Text(text = state.getSummaryMessage(), modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
                ) {
                    if(isAgainOptionAvailable) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onEvent(StudySummaryEvent.OnTryAgain)
                            },
                        ) {
                            Text(text = "TRY AGAIN")
                            Icon(imageVector = SWDIcons.Clear, contentDescription = null)
                        }
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onEvent(StudySummaryEvent.OnDoneSummary) }
                    ) {
                        Text(text = "DONE")
                        Icon(imageVector = SWDIcons.Check, contentDescription = null)
                    }
                }
            }
        }
    }
}
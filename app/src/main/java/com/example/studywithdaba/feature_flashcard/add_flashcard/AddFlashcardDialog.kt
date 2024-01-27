package com.example.studywithdaba.feature_flashcard.add_flashcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions

@Composable
fun AddFlashcardDialog(
    autoMove: Boolean,
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
) {
    val focusManager = LocalFocusManager.current
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
                    text = "Add deck",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                TextField(
                    maxLines = 7,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Definition")
                    },
                    isError = definitionError != null,
                    supportingText = {
                        if(definitionError != null)
                            Text(text = definitionError)
                        else
                            Text("*required")
                    },
                    value = definition,
                    onValueChange = {onDefinitionChange(it)},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    trailingIcon = {
                        if(definition.isNotBlank()) {
                            IconButton(onClick = { onDefinitionClear() }) {
                                Icon(SWDIcons.Clear, null)
                            }
                        }
                    },
                    label = {
                        Text(text = "Definition")
                    }
                )
                IconButton(onClick = { onSwap() }) {
                    Icon(SWDIcons.SwapVertically, null)
                }
                TextField(
                    maxLines = 7,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Answer")
                    },
                    isError = answerError != null,
                    supportingText = {
                        if(answerError != null)
                            Text(text = answerError)
                        else
                            Text("*required")
                    },
                    value = definition,
                    onValueChange = {onAnswerChange(it)},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    trailingIcon = {
                        if(answer.isNotBlank()) {
                            IconButton(onClick = { onAnswerClear() }) {
                                Icon(SWDIcons.Clear, null)
                            }
                        }
                    },
                    label = {
                        Text(text = "Answer")
                    }
                )


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
                        Text(text = "Add")
                    }
                }
            }
        }
    }
}



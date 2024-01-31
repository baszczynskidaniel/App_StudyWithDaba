package com.example.studywithdaba.feature_flashcard.flashcards_review_settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.studywithdaba.core.design_system.component.SelectionMenu
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions

@Composable
fun listItemModifier() = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(LocalDimensions.current.mediumClip))

@Composable
fun FlashcardsReviewSettingsDialog(
    onDismissRequest: (FlashcardsReviewSettingsState) -> Unit,
    initState: FlashcardsReviewSettingsState,
) {
    val viewModel: FlashcardsReviewSettingsViewModel = FlashcardsReviewSettingsViewModel(initState)
    val state = viewModel.state
    Dialog(
        onDismissRequest = { onDismissRequest(state.value) },
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
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
            ) {
                Row(
                    modifier = listItemModifier(),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = {
                        onDismissRequest(state.value)
                    }) {
                        Icon(SWDIcons.Clear, null)
                    }
                }
                ListItem(
                    modifier = listItemModifier(),
                    headlineContent = {
                        Text(text = "Shuffle cards")
                    },
                    leadingContent = {
                        Icon(imageVector = SWDIcons.Shuffle, contentDescription = null)
                    },
                    trailingContent = {
                        Switch(
                            checked = state.value.shuffle,
                            onCheckedChange = {
                                viewModel.onEvent(
                                    FlashcardsReviewSettingsEvent.OnShuffleChange(
                                        it
                                    )
                                )
                            }
                        )
                    }
                )
                ListItem(
                    modifier = listItemModifier(),
                    supportingContent = {
                        Text(text = "flashcard will be endlessly displayed")
                    },
                    headlineContent = {
                        Text(text = "Infinite repetition")
                    },
                    leadingContent = {
                        Icon(imageVector = SWDIcons.Infinity, contentDescription = null)
                    },
                    trailingContent = {
                        Switch(
                            checked = state.value.infiniteMode,
                            onCheckedChange = {
                                viewModel.onEvent(
                                    FlashcardsReviewSettingsEvent.OnInfinityModeChange(
                                        it
                                    )
                                )
                            }
                        )
                    }
                )
                ListItem(
                    modifier = listItemModifier(),
                    headlineContent = {
                        Text(text = "Only favourite flashcards")
                    },
                    leadingContent = {
                        Icon(imageVector = SWDIcons.FavouriteOutlined, contentDescription = null)
                    },
                    trailingContent = {
                        Switch(
                            checked = state.value.onlyFavourite,
                            onCheckedChange = {
                                viewModel.onEvent(
                                    FlashcardsReviewSettingsEvent.OnFavouriteChange(
                                        it
                                    )
                                )
                            }
                        )
                    }
                )
                ListItem(
                    modifier = listItemModifier(),
                    headlineContent = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
                        ) {
                            Text(text = "Asking side")
                            SelectionMenu(selected = state.value.visibility, onFilterItemClick = {
                                viewModel.onEvent(
                                    FlashcardsReviewSettingsEvent.OnVisibilityChange(
                                        it
                                    )
                                )
                            }, values = FlashcardRepeatedVisibility.values())
                        }
                    },
                )
                TextButton(
                    onClick = { onDismissRequest(state.value) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}




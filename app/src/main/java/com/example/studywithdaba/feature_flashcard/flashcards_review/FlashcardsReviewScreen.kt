package com.example.studywithdaba.feature_flashcard.flashcards_review

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardsReviewSettingsDialog
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryDialog


@Composable
fun FlashcardsReviewScreen(
    navController: NavController,
    viewModel: FlashcardsReviewViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        RepeatedFlashcardTopAppBar(
            onBack = {
                     viewModel.onEvent(FlashcardsReviewEvent.OnBack(navController))
            },
            onSettings = {
                viewModel.onEvent(FlashcardsReviewEvent.OnSettings(state.value.settings))
            },
            progress = if(state.value.settings.infiniteMode) {
                state.value.reviewSummary.getProgressInInfinityMode()
            } else {
                state.value.reviewSummary.getProgress(state.value.flashcardsInReview.size)
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
        ) {
            AssistChip(
                onClick = { viewModel.onEvent(FlashcardsReviewEvent.OnLastCard) },
                label = { Text(text = "Last flashcard") },
                trailingIcon = { Icon(SWDIcons.Undo, null) })
            AssistChip(
                onClick = { viewModel.onEvent(FlashcardsReviewEvent.OnFinishReview) },
                label = { Text(text = "go to summary") },
                trailingIcon = { Icon(SWDIcons.Next, null) })



        }
        Column(
            modifier = Modifier
                .padding(LocalDimensions.current.defaultPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlippableFlashcard(
                flashcard = state.value.currentFlashcard!!.flashcard,
                onFavouriteChange = {
                    viewModel.onEvent(FlashcardsReviewEvent.OnFavouriteChange(it))
                },
                isFrontVisible = state.value.isCurrentFlashcardFrontVisible,
                onClick = {
                    viewModel.onEvent(FlashcardsReviewEvent.OnFlashcardClick(it))
                }, modifier = Modifier.weight(1f, false))
            Answer(
                onCorrectClick = { viewModel.onEvent(FlashcardsReviewEvent.OnCorrectAnswer) },
                onWrongClick = { viewModel.onEvent(FlashcardsReviewEvent.OnWrongAnswer) }

            )
        }

    }
    if(state.value.showSettingsDialog) {
        FlashcardsReviewSettingsDialog(
            onDismissRequest = {
                viewModel.onEvent(FlashcardsReviewEvent.OnSettingsDismiss(it))
            },
            initState = state.value.settings
        )
    }
    if(state.value.showSummaryDialog) {
        StudySummaryDialog(
            onEvent = {
                      viewModel.onEvent(FlashcardsReviewEvent.OnSummaryDialogEvent(it, navController))
            },
            state = state.value.reviewSummary,
            label = "Review summary",
            isAgainOptionAvailable = true
        )
    }

}

@Composable
fun FlippableFlashcard(
    modifier: Modifier = Modifier,
    flashcard: Flashcard,
    isFrontVisible: Boolean,
    onClick: (Boolean) -> Unit,
    onFavouriteChange: (Boolean) -> Unit,
    showFavourite: Boolean = true,
) {

    val rotation by animateFloatAsState(targetValue = if(isFrontVisible) 180f else 0f, animationSpec = tween(500),
        label = "Flipping flashcard animation"
    )
    Card(
        modifier = Modifier
            .size(500.dp, 750.dp)
            .graphicsLayer {

                rotationY = rotation
                cameraDistance = 20 * density
            }
            .clickable {
                onClick(isFrontVisible)
            }
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalDimensions.current.defaultPadding)
                .graphicsLayer {
                    if (rotation > 90f) {
                        rotationY = 180f
                    }
                }
            ,
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
        ) {
            Box() {
                IconToggleButton(checked = false, onCheckedChange = { onFavouriteChange(flashcard.favourite)}, modifier = Modifier.align(Alignment.TopEnd)) {
                    if(flashcard.favourite) {
                        Icon(imageVector = SWDIcons.FavouriteFilled, contentDescription = null)
                    } else {
                        Icon(imageVector = SWDIcons.FavouriteOutlined, contentDescription = null)
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                    ,

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if(rotation < 90f) flashcard.back else flashcard.front,
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .verticalScroll(rememberScrollState())
                    )
                }
            }

        }
    }
}



@Composable
fun Answer(
    modifier: Modifier = Modifier,
    onCorrectClick: () -> Unit,
    onWrongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .navigationBarsPadding()
        ,
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
    ) {
        FilledIconButton(onClick = { onWrongClick() }, modifier = Modifier
            .height(LocalDimensions.current.bigIconButton)
            .fillMaxWidth()
            .weight(1f)) {
            Icon(imageVector = SWDIcons.Clear, contentDescription = null, modifier= Modifier.size(
                LocalDimensions.current.bigIcon))
        }
        FilledIconButton(onClick = { onCorrectClick() },  modifier = Modifier
            .height(LocalDimensions.current.bigIconButton)
            .fillMaxWidth()
            .weight(1f)) {
            Icon(imageVector = SWDIcons.Check, contentDescription = null, modifier= Modifier.size(
                LocalDimensions.current.bigIcon))
        }
    }
}

@Composable
fun RepeatedFlashcardTopAppBar(
    onBack: () -> Unit,
    onSettings: () -> Unit,
    progress: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
    ) {
        IconButton(onClick = { onBack() }) {
            Icon(SWDIcons.Back, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = progress, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier
            .fillMaxWidth()
            .weight(1f))
        IconButton(onClick = { onSettings() }) {
            Icon(SWDIcons.SettingsFilled, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

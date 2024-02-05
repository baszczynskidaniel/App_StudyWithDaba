package com.example.studywithdaba.feature_flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.feature_flashcard.flashcards_review.Answer
import com.example.studywithdaba.feature_flashcard.flashcards_review.FlashcardsReviewEvent
import com.example.studywithdaba.feature_flashcard.flashcards_review.FlippableFlashcard
import com.example.studywithdaba.feature_flashcard.flashcards_review.RepeatedFlashcardTopAppBar
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardsReviewSettingsDialog
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryDialog

@Composable
fun RepeatScreen(
    navController: NavController,
    viewModel: RepeatViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
        ,
    ) {
        RepeatFlashcardTopBar(
            onBack = {
                viewModel.onEvent(RepeatEvent.OnBack(navController))
            },

            progress = state.value.reviewSummary.getProgress(state.value.flashcards.size)
        )

        Column(
            modifier = Modifier
                .padding(LocalDimensions.current.defaultPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FlippableFlashcard(
                flashcard = state.value.currentFlashcard,
                onFavouriteChange = {

                },
                showFavourite = false,
                isFrontVisible = state.value.isCurrentFlashcardFrontVisible,
                onClick = {
                    viewModel.onEvent(RepeatEvent.OnFlashcardClick(it))
                }, modifier = Modifier.weight(1f, false))
            Answer(
                onCorrectClick = { viewModel.onEvent(RepeatEvent.OnCorrectAnswer) },
                onWrongClick = { viewModel.onEvent(RepeatEvent.OnWrongAnswer) },
                enabled = !state.value.showSummaryDialog
            )
        }

    }

    if(state.value.showSummaryDialog) {
        StudySummaryDialog(
            onEvent = {
                viewModel.onEvent(RepeatEvent.OnSummaryDialogEvent(it, navController))
            },
            state = state.value.reviewSummary,
            label = "Review summary",
            isAgainOptionAvailable = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatFlashcardTopBar(
    onBack: () -> Unit,
    progress: String
) {
    CenterAlignedTopAppBar(title = { Text(text = progress) }, navigationIcon = {
        IconButton(onClick = { onBack() }) {
            Icon(SWDIcons.Back, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    })

}

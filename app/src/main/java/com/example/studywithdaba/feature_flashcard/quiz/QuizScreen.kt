package com.example.studywithdaba.feature_flashcard.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studywithdaba.core.design_system.component.AutoResizedText
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.feature_flashcard.study_summary.StudySummaryDialog


@Composable
fun QuizScreen(
   navController: NavController,
   viewModel: QuizViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
        ,

        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
    ) {

        TopQuizBar(onBack = {
            viewModel.onEvent(QuizEvent.OnBack(navController))
        },  progress = state.progressMessage)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalDimensions.current.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight(0.35f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(LocalDimensions.current.defaultPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AutoResizedText(
                        text = state.currentQuestion!!.question, style = MaterialTheme.typography.displayMedium,
                        minFontSize = MaterialTheme.typography.titleLarge.fontSize,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
            ) {
                items(state.currentQuestion!!.answers.size) {index ->
                    AnswerItem(
                        onClick = {
                            viewModel.onEvent(QuizEvent.OnClickAnswer(index))
                        },
                        answer = state.currentQuestion.answers[index],
                        isAnswerCorrect = state.currentQuestion.isAnswerWithIndexCorrect(index),
                        selected = index == state.currentPickedAnswer,
                        showResult = state.showResult
                    )
                }
            }
            Button(onClick = { viewModel.onEvent(QuizEvent.OnCheck) }, modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),  ) {
                Text(text = state.checkContinueButtonName)
            }
        }
    }
    if(state.showResult) {
        QuizResultBottomSheet(
            onDismissRequest = { viewModel.onEvent(QuizEvent.OnDismissResult) },
            correctAnswerMessage = state.currentQuestion!!.getCorrectAnswerText(),
            isAnswerCorrect = state.isCurrentPickedAnswerCorrect,
            onDoneClick = { viewModel.onEvent(QuizEvent.OnDismissResult) }
        )
    }
    if(state.showSummary) {
        StudySummaryDialog(
            onEvent = { viewModel.onEvent(QuizEvent.OnSummaryDialogEvent(it, navController)) },
            state = state.summary,
            label = "Quiz result",
            isAgainOptionAvailable = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultBottomSheet(
    onDismissRequest: () -> Unit,
    correctAnswerMessage: String,
    isAnswerCorrect: Boolean,
    onDoneClick: () -> Unit,

) {
    var contentColor by remember {
        mutableStateOf(Color(0xffffffff))
    }

    var containerColor by remember {
        mutableStateOf(Color(0xffffffff))
    }
    contentColor = if(isAnswerCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    containerColor = if(isAnswerCorrect) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onError

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() }

    ) {
        Column(
            modifier = Modifier
                .padding(LocalDimensions.current.defaultPadding)
                .navigationBarsPadding()
            ,
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
        ) {
            Row(

            ) {
                Text(
                    text = if (isAnswerCorrect)
                        "You are right!"
                    else
                        "You are wrong!",
                    color = contentColor,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                if(isAnswerCorrect)
                    Icon(imageVector = SWDIcons.Check, contentDescription = null, tint = contentColor)
                else
                    Icon(imageVector = SWDIcons.Clear, contentDescription = null, tint = contentColor)

            }
            Text(text = "the correct answer is: $correctAnswerMessage")
            Button(onClick = { onDoneClick() },  modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = contentColor,
                    contentColor = containerColor
                )
                ) {
                Text(text = "GOT IT")
            }
        }
    }
}

@Composable
fun AnswerItem(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit,
    answer: String,
    isAnswerCorrect: Boolean,
    selected: Boolean,
    showResult: Boolean, 
    enabled: Boolean = true,
) {
    
    var contentColor by remember {
        mutableStateOf(Color(0xffffffff))
    }
    var containerColor by remember {
        mutableStateOf(Color(0xffffffff))
    }
    
    containerColor = when {
        selected && !showResult -> MaterialTheme.colorScheme.secondaryContainer
        !selected && !showResult -> Color.Transparent
        selected && isAnswerCorrect -> MaterialTheme.colorScheme.secondary
        selected && !isAnswerCorrect -> MaterialTheme.colorScheme.errorContainer
        !selected && isAnswerCorrect -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }

    contentColor = when {
        selected && !showResult -> MaterialTheme.colorScheme.onSecondaryContainer
        !selected && !showResult -> MaterialTheme.colorScheme.onBackground
        selected && isAnswerCorrect -> MaterialTheme.colorScheme.onSecondary
        selected && !isAnswerCorrect-> MaterialTheme.colorScheme.onErrorContainer
        !selected && isAnswerCorrect -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onBackground
    }

    OutlinedButton(
        shape = RoundedCornerShape(LocalDimensions.current.mediumClip),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
            containerColor = containerColor,
        ),
        border = BorderStroke(1.dp, contentColor),
        modifier = modifier,
        onClick = { onClick(selected) }) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = { onClick(selected) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = contentColor,
                    unselectedColor = contentColor,
                )
            )
            Text(text = answer, style = MaterialTheme.typography.titleMedium, modifier = Modifier
                .fillMaxWidth()
                .weight(1f))
            if(showResult) {
                if(selected && isAnswerCorrect || !selected && isAnswerCorrect)
                    Icon(imageVector = SWDIcons.Check, contentDescription = null)
                if(selected && !isAnswerCorrect)
                    Icon(imageVector = SWDIcons.Clear, contentDescription = null)
            }
        }
    }
}

@Composable
internal fun TopQuizBar(
    onBack: () -> Unit,

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
        Text(
            text = progress,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }

}

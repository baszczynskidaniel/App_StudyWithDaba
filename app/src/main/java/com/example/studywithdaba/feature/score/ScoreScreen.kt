package com.example.studywithdaba.feature.score

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


data class ScoreState(
    val wrong: Int = 0,
    val correct: Int = 0,
    val score: Float = 0f,
    val showTryAgain: Boolean = true,
)

sealed class ScoreEvent {
    data class OnFinishReview(val navController: NavController): ScoreEvent()
    data class OnTryAgain(val navController: NavController): ScoreEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    wrong: Int,
    correct: Int,
    score: Float,
    showTryAgain: Boolean,
    onFinishReview: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(
                bottom = LocalDimensions.current.defaultPadding,
                start = LocalDimensions.current.defaultPadding,
                end = LocalDimensions.current.defaultPadding,
            )
    ) {

        Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularArc(size = 300.dp, progressInPercentage = score.toFloat())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
            ) {
                ReviewItem(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    icon = SWDIcons.Clear,
                    score = wrong,
                    description = "Wrong"
                )
                ReviewItem(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    icon = SWDIcons.Check,
                    score = correct,
                    description = "Correct"
                )
            }
        }
        Button(onClick = { onFinishReview() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Finish review")
        }
        if(showTryAgain) {
            Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
            OutlinedButton(onClick = {
                onTryAgain()
            }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Try again")
            }
        }
    }
}

@Composable
fun CircularArc(
    modifier: Modifier = Modifier,
    size: Dp,
    progressInPercentage: Float,

) {
    val correctColor = MaterialTheme.colorScheme.primary
    val wrongColor = MaterialTheme.colorScheme.primary.copy(0.2f)
    val progressToAngle = progressInPercentage / 100f * 360f
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.toPx() / 2
            val centerY = size.toPx() / 2
            val radius = size.toPx() / 2

            val angleRadians = Math.toRadians(progressToAngle.toDouble())

            val intersectionX = centerX + (radius * cos(angleRadians + Math.PI + Math.PI / 2)).toFloat()
            val intersectionY = centerY + (radius * sin(angleRadians + Math.PI + Math.PI / 2)).toFloat()

            drawArc(
                color = correctColor,
                startAngle = 270f,
                sweepAngle = progressToAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(size.toPx(), size.toPx()),
                style = Stroke(width = 20f)
            )

            drawArc(
                color = wrongColor,
                startAngle = 270f + progressToAngle,
                sweepAngle = 360f - progressToAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(size.toPx(), size.toPx()),
                style = Stroke(width = 20f)
            )

            drawCircle(
                color = correctColor,
                radius = 40f,
                center = Offset(intersectionX, intersectionY)
            )
        }
        Text(text = "${progressInPercentage.roundToInt()} %", modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displayLarge)
    }
}

@Composable
internal fun ReviewItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    score: Int,
    description: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(LocalDimensions.current.bigIconButton),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(text = score.toString(), color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.displaySmall)
        Text(text = description, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview
@Composable
internal fun ScoreScreenPreview() {
    StudyWithDabaTheme(
        darkTheme = true,
        dynamicColor = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScoreScreen(wrong = 2, correct = 5, score = 55f, false, {}, {})
        }
    }
}
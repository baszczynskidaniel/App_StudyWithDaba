package com.example.studywithdaba.core.design_system.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit


internal const val DEFAULT_RESIZE_MULTIPLIER = 0.95

@Composable
fun AutoResizedText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = style.color,
    minFontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    resizeMultiplier: Double = DEFAULT_RESIZE_MULTIPLIER,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDisplayText by remember {
        mutableStateOf(false)
    }

    Text(
        text = text,
        color = color,
        maxLines =  maxLines,
        minLines = minLines,
        softWrap = softWrap,
        overflow = overflow,
        modifier = modifier.drawWithContent {
            if(shouldDisplayText) {
                drawContent()
            }
        },
        style = resizedTextStyle,

        onTextLayout = { result ->
            val resizedFontSize = resizedTextStyle.fontSize * resizeMultiplier

            if ((result.hasVisualOverflow) && resizedFontSize > minFontSize) {
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedFontSize,
                    lineHeight = resizedTextStyle.lineHeight * resizeMultiplier,
                )
            } else {
                shouldDisplayText = true

            }
            onTextLayout(result)
        }
    )
}


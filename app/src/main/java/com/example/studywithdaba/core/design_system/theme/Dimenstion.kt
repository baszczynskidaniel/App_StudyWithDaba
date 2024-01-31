package com.example.studywithdaba.core.design_system.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import javax.annotation.concurrent.Immutable

@Immutable
data class Dimensions(
    val defaultPadding: Dp = 12.dp,
    val doubleDefaultPadding: Dp = defaultPadding * 2,
    val halfDefaultPadding: Dp = defaultPadding / 2f,
    val dialogMaxWidth: Dp = 400.dp,
    val highlightWidth: Dp = 2.dp,
    val bigClip: Dp = 32.dp,
    val mediumClip: Dp = 16.dp,
    val dividerThickness: Dp = 0.5.dp,
    val alertDialogWidth: Dp = 400.dp,
    val bigIconButton: Dp = 72.dp,
    val bigIcon: Dp = 36.dp,
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }
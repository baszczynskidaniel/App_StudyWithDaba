package com.example.richtextfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun RichTextField(
    modifier: Modifier = Modifier,
    richTextValue: RichTextValue,
    onValueChange: (TextFieldValue) -> Unit,
    spanStyle: SpanStyle = SpanStyle(),
    paragraphStyle: ParagraphStyle = ParagraphStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
    placeholder: @Composable (() -> Unit)? = null,

    ) {
    Box() {

        BasicTextField(
            modifier = modifier,
            value = richTextValue.text.value,
            onValueChange = onValueChange,
            textStyle = getTextStyleFromSpanStyleAndParagraphStyle(spanStyle, paragraphStyle),
            enabled = enabled,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            decorationBox = decorationBox,
            visualTransformation = RichTextFieldTransformation(richTextValue),
        )


    }
}

@Composable
fun RichTextFieldWithHint(
    modifier: Modifier = Modifier,
    richTextValue: RichTextValue,
    onValueChange: (TextFieldValue) -> Unit,
    spanStyle: SpanStyle = SpanStyle(),
    paragraphStyle: ParagraphStyle = ParagraphStyle(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    hint: String = "",
    hintStyle: TextStyle = TextStyle()
) {
    RichTextField(
        modifier = modifier,
        richTextValue = richTextValue,
        onValueChange = onValueChange,
        spanStyle = spanStyle,
        paragraphStyle = paragraphStyle,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = { innerTextField ->
           HintDecorationBox(
                value = richTextValue,
                hint = hint,
                hintStyle = hintStyle,
                innerTextField = innerTextField
            )
        },
    )
}

@Composable
fun HintDecorationBox(
    value: RichTextValue,
    hint: String,
    hintStyle: TextStyle,
    innerTextField: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (value.text.value.text.isBlank() && !hint.isNullOrBlank()) {
            Text(
                text = hint,
                style = hintStyle
            )
        }
        innerTextField.invoke()
    }
}

fun getTextStyleFromSpanStyleAndParagraphStyle(
    spanStyle: SpanStyle,
    paragraphStyle: ParagraphStyle
): TextStyle {
    return TextStyle(
        color = spanStyle.color,
        fontWeight = spanStyle.fontWeight,
        fontStyle = spanStyle.fontStyle,
        fontSynthesis = spanStyle.fontSynthesis,
        fontFamily = spanStyle.fontFamily,
        fontFeatureSettings = spanStyle.fontFeatureSettings,
        letterSpacing = spanStyle.letterSpacing,
        baselineShift = spanStyle.baselineShift,
        textGeometricTransform = spanStyle.textGeometricTransform,
        localeList = spanStyle.localeList,
        background = spanStyle.background,
        textDecoration = spanStyle.textDecoration,
        shadow = spanStyle.shadow,
        textAlign = paragraphStyle.textAlign,
        textDirection = paragraphStyle.textDirection,
        lineHeight = paragraphStyle.lineHeight,
        textIndent = paragraphStyle.textIndent
    )
}

fun getSpanStyleFromTextStyle(textStyle: TextStyle): SpanStyle {
    return SpanStyle(
        color = textStyle.color,
        fontWeight = textStyle.fontWeight,
        fontStyle = textStyle.fontStyle,
        fontSynthesis = textStyle.fontSynthesis,
        fontFamily = textStyle.fontFamily,
        fontFeatureSettings = textStyle.fontFeatureSettings,
        letterSpacing = textStyle.letterSpacing,
        baselineShift = textStyle.baselineShift,
        textGeometricTransform = textStyle.textGeometricTransform,
        localeList = textStyle.localeList,
        background = textStyle.background,
        textDecoration = textStyle.textDecoration,
        shadow = textStyle.shadow,
    )
}




package com.example.richtextfield

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.TextUnit

class SpanStyleTextTokenManager(defaultValue: SpanStyle) : TextTokenManager<SpanStyle>(defaultValue) {
    fun<T> setStyleParameterInRange(start: Int, end: Int, value: T, argumentToUpdate: SpanStyleArgument) {
        val changeTokens: MutableList<StyleToken<SpanStyle>> = mutableListOf()
        val valueAtStart = getValueAtIndex(start).copy()
        if(!doesTokenWithStartExists(start))
            tokens.add(StyleToken(start, getValueBeforeIndex(start)!!))
        val tokenAtEnd = getValueAtIndex(end + 1)
        for(counter in tokens.indices) {

            if(tokens[counter].start >= start && tokens[counter].start < end) {
                changeTokens.add(tokens[counter])
                when(argumentToUpdate) {
                    SpanStyleArgument.COLOR -> tokens[counter].value = tokens[counter].value.copy(color = value as Color)
                    SpanStyleArgument.FONT_SIZE -> tokens[counter].value = tokens[counter].value.copy(fontSize = value as TextUnit)
                    SpanStyleArgument.FONT_WEIGHT -> tokens[counter].value = tokens[counter].value.copy(fontWeight = value as FontWeight?)
                    SpanStyleArgument.FONT_STYLE -> tokens[counter].value = tokens[counter].value.copy(fontStyle = value as FontStyle?)
                    SpanStyleArgument.FONT_SYNTHESIS -> tokens[counter].value = tokens[counter].value.copy(fontSynthesis = value as FontSynthesis?)
                    SpanStyleArgument.FONT_FAMILY -> tokens[counter].value = tokens[counter].value.copy(fontFamily = value as FontFamily?)
                    SpanStyleArgument.FONT_FEATURE_SETTINGS -> tokens[counter].value = tokens[counter].value.copy(fontFeatureSettings = value as String?)
                    SpanStyleArgument.LETTER_SPACING -> tokens[counter].value = tokens[counter].value.copy(letterSpacing = value as TextUnit)
                    SpanStyleArgument.BASELINE_SHIFT -> tokens[counter].value = tokens[counter].value.copy(baselineShift = value as BaselineShift?)
                    SpanStyleArgument.TEXT_GEOMETRIC_TRANSFORM -> tokens[counter].value = tokens[counter].value.copy(textGeometricTransform = value as TextGeometricTransform?)
                    SpanStyleArgument.LOCALE_LIST -> tokens[counter].value = tokens[counter].value.copy(localeList = value as LocaleList?)
                    SpanStyleArgument.BACKGROUND -> tokens[counter].value = tokens[counter].value.copy(background = value as Color)
                    SpanStyleArgument.TEXT_DECORATION -> tokens[counter].value = tokens[counter].value.copy(textDecoration = value as TextDecoration?)
                    SpanStyleArgument.SHADOW -> tokens[counter].value = tokens[counter].value.copy(shadow = value as Shadow?)
                }
            }
        }




        if(!doesTokenWithStartExists(end)) {
            if(changeTokens.size >= 2) {
                tokens.add(StyleToken(end, tokenAtEnd!!))
            } else {

                tokens.add(StyleToken(end,valueAtStart))
            }
        }


        processAndFilterTokens()
    }
}

enum class SpanStyleArgument {
    COLOR,
    FONT_SIZE,
    FONT_WEIGHT,
    FONT_STYLE,
    FONT_SYNTHESIS,
    FONT_FAMILY,
    FONT_FEATURE_SETTINGS,
    LETTER_SPACING,
    BASELINE_SHIFT,
    TEXT_GEOMETRIC_TRANSFORM,
    LOCALE_LIST,
    BACKGROUND,
    TEXT_DECORATION,
    SHADOW,
}
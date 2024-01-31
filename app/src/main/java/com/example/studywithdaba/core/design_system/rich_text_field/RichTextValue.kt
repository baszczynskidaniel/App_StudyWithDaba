package com.example.richtextfield

import android.util.Log
import android.util.TypedValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.graphics.toColor
import com.google.gson.Gson

data class RichTextString(
    val start: Int,
    val fontWeight: Int,
    val isItalic: Boolean,
    val isUnderline: Boolean,
    val color: Int?,
    val background: Int,
) {
    companion object {
        fun toJsonString(richTextString: List<RichTextString>): String {
            val gson = Gson()
            val jsonString = gson.toJson(richTextString)
            return jsonString
        }


        fun toRichTextString(jsonString: String): List<RichTextString> {
            val gson = Gson()
            val listType = Array<RichTextString>::class.java
            val richTextStringArray: Array<RichTextString> = gson.fromJson(jsonString, listType)
            val reversedList = richTextStringArray.toMutableList()
            reversedList.sortBy { it.start }
            return reversedList
        }

        fun toSpanStyle(defaultStyle: SpanStyle, richTextString: RichTextString): SpanStyle {
            if(richTextString.color == null)
                return defaultStyle.copy(
                    //color = if(Color(richTextString.color),
                    fontWeight = FontWeight(richTextString.fontWeight),
                    fontStyle = if(richTextString.isItalic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = if(richTextString.isUnderline) TextDecoration.Underline else null,
                    background = Color(richTextString.background)

                )
            else
                return defaultStyle.copy(
                    color = Color(richTextString.color),
                    fontWeight = FontWeight(richTextString.fontWeight),
                    fontStyle = if(richTextString.isItalic) FontStyle.Italic else FontStyle.Normal,
                    textDecoration = if(richTextString.isUnderline) TextDecoration.Underline else null,
                    background = Color(richTextString.background)

                )

        }
    }


}

class RichTextValue {
    var text = mutableStateOf(TextFieldValue(""))
    var defaultStyle: SpanStyle
    var styleManager: SpanStyleTextTokenManager


    fun toDataClassList(): List<RichTextString> {
        val result = mutableListOf<RichTextString>()
        styleManager.tokens.forEach {token ->
            val index = token.start
            val fontWeight = token.value.fontWeight?.weight ?: FontWeight.Normal.weight
            val isItalic = token.value.fontStyle == FontStyle.Italic
            val isUnderline = token.value.textDecoration == TextDecoration.Underline
            Log.d("token", "${token.value.color.alpha}")
            Log.d("color", "${token.value.color}")
            val color = if(token.value.color.alpha == 0.9882353f) token.value.color.toArgb() else null
            val fontSize = token.value.fontSize.value
            val background = token.value.background.toArgb()
            result.add(
                RichTextString(
                    start = index,
                    fontWeight = fontWeight,
                    isItalic = isItalic,
                    isUnderline = isUnderline,
                    color = color,
                    background = background,
                )
            )
        }
        return result
    }
    constructor(defaultStyle: SpanStyle, jsonString: String, text: String) {
        this.defaultStyle = defaultStyle
        val richTextString = RichTextString.toRichTextString(jsonString)
        styleManager = SpanStyleTextTokenManager(defaultStyle)
        this.text.value = TextFieldValue(text)
        styleManager.tokens.clear()
        richTextString.forEach {  text ->
            val token = TextToken<SpanStyle>(
                start = text.start,
                value = RichTextString.toSpanStyle(defaultStyle, text)
            )
            styleManager.tokens.add(token)
        }
    }


    constructor(defaultStyle: TextStyle) {
        this.defaultStyle = getSpanStyleFromTextStyle(defaultStyle)
        styleManager = SpanStyleTextTokenManager(this.defaultStyle)
    }

    var isInSelection: Boolean = false

    constructor(defaultStyle: SpanStyle) {
        this.defaultStyle = defaultStyle
        styleManager = SpanStyleTextTokenManager(defaultStyle)

    }
    fun getSelectedStyle(): SpanStyle {
        return styleManager.selectedValue
    }

    fun<T> updateSelectionWithValue(value: T, argumentToUpdate: SpanStyleArgument) {
        if(isInSelection) {
            styleManager.setStyleParameterInRange(text.value.selection.start, text.value.selection.end, value, argumentToUpdate)
        }
    }

    fun updateSelectionWithStyle(style: SpanStyle) {
        if(isInSelection) {
            styleManager.updateSelection(text.value.selection.start, text.value.selection.end, style)
        }
    }

    fun update(newText: TextFieldValue, style: SpanStyle) {

        val step = newText.text.length - text.value.text.length
        text.value = newText
        isInSelection = text.value.selection.end != text.value.selection.start
        styleManager.update(step, text.value.selection, style, text.value.text.length)
    }
    fun buildAnnotatedStringWithStyleManager(text: String): AnnotatedString {
        return styleManager.buildAnnotatedString(text)
    }
}

data class TextToken<T>(
    var start: Int,
    var value: T,
)


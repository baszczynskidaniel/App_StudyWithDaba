package com.example.richtextfield

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.withStyle

open class TextTokenManager<T>(var defaultValue: T) {

    var selectedValue: T = defaultValue
    var tokens: MutableList<TextToken<T>> = mutableListOf(TextToken(0, defaultValue))
    var textLength: Int = 0


    fun update(step: Int, selection: TextRange, value: T, textLength: Int) {

        this.textLength = textLength
        when {
            step > 0 -> insertToken(selection.end - step, value, step)
            step < 0 -> moveBackwardTokenByStepFromIndex(selection.end, -step)
            else -> {

            }
        }
        selectedValue = if (selection.end == selection.start)
            getValueAtIndex(selection.end - 1)
        else
            getValueAtRange(selection.start, selection.end)
    }

    fun updateSelection(start: Int, end: Int, value: T) {
        setValueInRange(start, end, value)
    }

    fun getValueAtRange(start: Int, end: Int): T {
        var value: T? = null
        if (!doesTokenWithStartExists(start) && start != 0)
            value = getValueBeforeIndex(start)
        for (token in tokens) {
            if (token.start in start until end) {
                if (value == null)
                    value = token.value
                else
                    return defaultValue
            }
        }
        return value ?: defaultValue
    }

    fun buildAnnotatedString(text: String): AnnotatedString {
        val builder = AnnotatedString.Builder();
        if (text.isBlank())
            return builder.toAnnotatedString()
        for (i in tokens.indices) {
            if (tokens[i].start >= text.length)
                break
            builder.withStyle(tokens[i].value as SpanStyle) {
                if (i != tokens.lastIndex)
                    if (tokens[i + 1].start >= text.length)
                        append(text.substring(tokens[i].start))
                    else
                        append(text.substring(tokens[i].start, tokens[i + 1].start))
                else
                    append(text.substring(tokens[i].start))
            }
        }
        return builder.toAnnotatedString()
    }

    private fun moveBackwardTokenByStepFromIndex(index: Int, step: Int) {
        var counter = 0
        val removedTokens: MutableList<TextToken<T>> = mutableListOf()
        while (counter < tokens.size) {
            if (tokens[counter].start < index) {
                counter++
                continue
            }
            if (tokens[counter].start - step < index) {
                removedTokens.add(tokens.removeAt(counter))
            } else {
                tokens[counter].start -= step
                counter++
            }
        }
        if (!removedTokens.isEmpty()) {
            val firstRemovedToken = removedTokens.first()
            if (!doesTokenWithStartExists(index)) {
                tokens.add(TextToken(index, firstRemovedToken.value))
            }
        }

        if (!doesTokenWithStartExists(0)) {
            if (tokens.isEmpty())
                tokens.add(TextToken(0, defaultValue))
            else
                tokens.add(TextToken(0, getValueAfterIndex(0)!!))
        }
        processAndFilterTokens()
    }


    private fun setValueInRange(start: Int, end: Int, value: T) {
        var counter = 0
        val removedTokens: MutableList<TextToken<T>> = mutableListOf()
        val isTokenFromEnd = getValueAfterIndex(end - 1) != null
        while (counter < tokens.size) {
            if (tokens[counter].start >= start && tokens[counter].start < end) {
                removedTokens.add(tokens[counter])
                tokens.removeAt(counter)
                continue

            }
            counter++
        }
        tokens.add(TextToken(start, value))
        if (!doesTokenWithStartExists(end) && removedTokens.isNotEmpty()) {
            tokens.add(TextToken(end, removedTokens.first().value))
        } else if (isTokenFromEnd && getValueBeforeIndex(start + 1) != null) {
            tokens.add(TextToken(end, getValueBeforeIndex(start)!!))
        }



        processAndFilterTokens()
    }

    private fun moveForwardTokenByStepFromIndex(
        index: Int,
        step: Int,
        includeZeroIndex: Boolean = false
    ) {
        if (includeZeroIndex && index == 0) {
            tokens.first().start += step
        }
        for (i in 1 until tokens.size) {
            if (tokens[i].start >= index)
                tokens[i].start += step;
        }
    }

    protected fun getValueBeforeIndex(index: Int): T? {
        var lastValue: T? = null
        for (token in tokens) {
            if (token.start >= index) {
                return lastValue
            }
            lastValue = token.value
        }
        return lastValue
    }

    protected fun getValueAtIndex(index: Int): T {
        return try {
            getValueBeforeIndex(index + 1)!!
        } catch (e: Exception) {
            return tokens[0].value
        }

    }

    protected fun getValueAfterIndex(index: Int): T? {
        for (token in tokens) {
            if (token.start > index) {
                return token.value
            }
        }
        return null
    }


    private fun insertToken(index: Int, value: T, step: Int) {
        if (step <= 0)
            return
        val token = TextToken<T>(index, value)
        val tokenBeforeIndex = getTokenBeforeIndex(index)
        val doesTokenAtIndexExists = doesTokenWithStartExists(index)
        moveForwardTokenByStepFromIndex(index, step, true)
        tokens.add(token)
        if (!doesTokenAtIndexExists && tokenBeforeIndex != null)
            tokens.add(TextToken(index + step, tokenBeforeIndex.value))

        processAndFilterTokens()
    }

    protected fun doesTokenWithStartExists(start: Int): Boolean {
        for (token in tokens) {
            if (token.start == start)
                return true
        }
        return false
    }

    fun getTokenBeforeIndex(index: Int): TextToken<T>? {
        var lastToken: TextToken<T>? = null
        for (token in tokens) {
            if (token.start >= index) {
                return lastToken
            }
            lastToken = token
        }
        return lastToken

    }

    protected fun processAndFilterTokens() {
        tokens.sortBy { it.start }
        var counter = 1
        var lastValue: T = tokens.first().value
        while (counter < tokens.size) {
            if (tokens[counter].value == lastValue || tokens[counter].start >= textLength) {
                tokens.removeAt(counter)
                continue
            } else {
                lastValue = tokens[counter].value
                counter++
            }
        }
    }
}
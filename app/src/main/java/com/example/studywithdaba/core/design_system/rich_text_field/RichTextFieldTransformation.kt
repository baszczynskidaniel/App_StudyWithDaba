package com.example.richtextfield

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RichTextFieldTransformation (var richTextValue: RichTextValue): VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            richTextValue.buildAnnotatedStringWithStyleManager(text.toString()),
            OffsetMapping.Identity
        )
    }
}
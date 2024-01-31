package com.example.richtextfield

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme

@Preview
@Composable
fun ExampleScreen() {
    var style by remember {
        mutableStateOf(SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal, fontStyle = FontStyle.Normal, color = Color.Black))
    }
    var value by remember {
        mutableStateOf(RichTextValue(style))
    }
    var json by remember {
        mutableStateOf("")
    }
    StudyWithDabaTheme {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        var fontWeightOptions =
            listOf<FontWeight>(FontWeight.Normal, FontWeight.ExtraLight, FontWeight.Bold)
        Text(text = "Bold", fontSize = 15.sp, color = Color.White)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (fontWeight in fontWeightOptions) {
                RadioButton(selected = style.fontWeight == fontWeight, onClick = {

                    style = style.copy(fontWeight = fontWeight)
                    value.updateSelectionWithValue(style.fontWeight, SpanStyleArgument.FONT_WEIGHT)
                })
                Text(text = fontWeight.weight.toString())
            }
        }

        var fontStyleOptions = listOf<FontStyle>(FontStyle.Normal, FontStyle.Italic)
        Text(text = "Style", fontSize = 15.sp, color = Color.White)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (fontStyle in fontStyleOptions) {
                RadioButton(selected = style.fontStyle == fontStyle, onClick = {
                    style = style.copy(fontStyle = fontStyle)
                    value.updateSelectionWithValue(style.fontStyle, SpanStyleArgument.FONT_STYLE)
                })
                Text(text = fontStyle.toString())
            }
        }

        var fontSizeOptions = listOf<TextUnit>(20.sp, 30.sp, 40.sp, 50.sp)
        Text(text = "fontSize", fontSize = 15.sp, color = Color.White)
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (fontSize in fontSizeOptions) {
                RadioButton(selected = style.fontSize == fontSize, onClick = {

                    style = style.copy(fontSize = fontSize)
                    value.updateSelectionWithValue(style.fontSize, SpanStyleArgument.FONT_SIZE)

                })
                Text(text = fontSize.toString())
            }
        }
        Text(text = json)
        RichTextField(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 70.dp)
                .padding(20.dp)
                .border(2.dp, Color.Red),
            richTextValue = value,
            onValueChange = {
                value.update(it, style)
                style = value.getSelectedStyle()
                val dataCLassList = value.toDataClassList()
                json = RichTextString.toJsonString(dataCLassList)
            },
            placeholder = {
                Text(
                    text = "Write here",
                    style = TextStyle(
                        color = Color(0xdd000000),
                        fontSize = 20.sp,
                    ),
                    modifier = Modifier
                        .padding(20.dp)

                )
            }
        )

    }
    }
}
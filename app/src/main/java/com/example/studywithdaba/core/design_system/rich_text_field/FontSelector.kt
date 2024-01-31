package com.example.richtextfield

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme

/*
TODO
    Row with buttons
    buttons type:
        toggle
        open dialog
        open selection menu
    Selection Row
 */

enum class SelectionPopUpMenu {
    NONE,
    FONT_SIZE,
    COLOR,
}

open class MenuItem<T> (var value: T) {
    fun onClick(): Unit {

    }
    
}


@Composable
fun TextFormattingToolbar(
    style: SpanStyle,
    onStyleChange: (SpanStyle) -> Unit,
    modifier: Modifier = Modifier,
    showColorPicker: Boolean,
    selectedColor: Color,
    defaultColor: Color,
    colors: List<Color>,
    onDefaultColor: Color,
    onColors: Color,
    onShowColorPickerChange: (Boolean) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Divider()
        AnimatedVisibility(showColorPicker) {
            ColorPicker(
                selectedColor = selectedColor,
                defaultColor = defaultColor,
                colors = colors,
                onItemClick = { onStyleChange(style.copy(color = it)) },
                onDefaultColor = onDefaultColor,
                onColors = onColors
            )
        }
        Divider(thickness = LocalDimensions.current.dividerThickness, modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier

                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(horizontal = LocalDimensions.current.defaultPadding)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center
        ) {
            ToggleButton(
                selected = style.fontWeight == FontWeight.Bold,
                onValueChange = {
                    val newFontWeight =
                        if (style.fontWeight == FontWeight.Bold) FontWeight.Normal else FontWeight.Bold
                    onStyleChange(style.copy(fontWeight = newFontWeight))
                },
                text = "B",
                textStyle = TextStyle(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.defaultPadding))
            ToggleButton(
                selected = style.fontStyle == FontStyle.Italic,
                onValueChange = {
                    val newFontStyle =
                        if (style.fontStyle == FontStyle.Italic) FontStyle.Normal else FontStyle.Italic
                    onStyleChange(style.copy(fontStyle = newFontStyle))
                },
                text = "I",
                textStyle = TextStyle(fontStyle = FontStyle.Italic)
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.defaultPadding))
            ToggleButton(
                selected = style.textDecoration == TextDecoration.Underline,
                onValueChange = {
                    val newTextDecoration =
                        if (style.textDecoration == TextDecoration.Underline) TextDecoration.None else TextDecoration.Underline
                    onStyleChange(style.copy(textDecoration = newTextDecoration))
                },
                text = "U",
                textStyle = TextStyle(textDecoration = TextDecoration.Underline)
            )
            Spacer(modifier = Modifier.width(LocalDimensions.current.defaultPadding))
            ColorPickerButton(onClick = { onShowColorPickerChange(!showColorPicker)}, color = selectedColor, selected = showColorPicker)
        }
        Divider(thickness = LocalDimensions.current.dividerThickness)
    }
}
internal val colors = listOf<Color>(
    Color(0xFFF56E30),
    Color(0xFFF5D430),
    Color(0xFFB0F530),
    Color(0xFF30F568),
    Color(0xFF30F5C7),
    Color(0xFF30D7F5),
    Color(0xFF30BDF5),
    Color(0xFF3730F5),
    Color(0xFFD130F5),
    Color(0xFFEE30F5),
    Color(0xffF5306B),
    Color(0xFFF53030),
)


@Composable
fun ColorPicker(
    selectedColor: Color,
    defaultColor: Color,
    colors: List<Color>,
    onItemClick: (Color) -> Unit,
    onDefaultColor: Color,
    onColors: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = LocalDimensions.current.defaultPadding)
        ,

        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
    ) {
        ColorPickerItem(color = defaultColor, selected = selectedColor == defaultColor, onClick = {onItemClick(defaultColor)}, onDefaultColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
        ) {
            colors.forEach {color ->
                ColorPickerItem(color = color, selected = selectedColor == color, onClick = {onItemClick(color)}, tint = onColors)
            }
        }
    }
}

@Composable
fun ColorPickerButton(
    onClick: () -> Unit,
    color: Color,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if(selected) 48.dp else 24.dp)
                .clip(CircleShape)
                .background(color)
                .align(Alignment.Center)
        ) {

        }
    }
}
@Composable
fun ColorPickerItem(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    tint: Color
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .size(48.dp)
    ) {
        val selectedModifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(0.3f))
            .padding(6.dp)
            .clip(CircleShape)
            .background(color)
            .align(Alignment.Center)
        val unselectedModifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .align(Alignment.Center)
        Box(
            modifier = if(selected) selectedModifier else unselectedModifier
        ) {
            if(selected)
                Icon(SWDIcons.Check, null, tint = tint, modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp))
        }
    }
}

@Composable
fun<T> SelectionRow(
    values: List<T>,
    valuesLabel: List<String>,
    selected: T,
    expanded: Boolean = true,
    modifier: Modifier = Modifier,
    onValueChange: (T) -> Unit = {},
    textStyle: TextStyle = TextStyle()
) {
    if (expanded) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center
        ) {


            for (i in values.indices) {
                ToggleButton(
                    modifier = Modifier.padding(2.dp),
                    selected = selected == values[i],
                    text = valuesLabel[i],
                    textStyle = TextStyle(fontSize = 24.sp),
                    onValueChange = { onValueChange(values[i]) }
                )
            }
        }
    }
}

@Preview
@Composable
fun TextStyleBar(

) {
    StudyWithDabaTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TextFormattingToolbar(
                style = SpanStyle(fontWeight = FontWeight.Bold),
                {},
                showColorPicker = true,
                defaultColor = MaterialTheme.colorScheme.onBackground,
                colors = colors,
                onShowColorPickerChange = {},
                selectedColor = colors[2],
                onColors = Color.Black,
                onDefaultColor = MaterialTheme.colorScheme.background
            )
        }
    }
}
@Composable
fun ToggleButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: () -> Unit,
    text: String,
    textStyle: TextStyle = TextStyle()
) {
    var background by remember {
        mutableStateOf(Color.Black)
    }
    var onBackground by remember {
        mutableStateOf(Color.Black)
    }
    background = if(!selected) Color.Transparent else MaterialTheme.colorScheme.primary
    onBackground = if(!selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary
    Box(
        modifier = Modifier

            .clickable(onClick = onValueChange)
            .then(modifier)
            .imePadding()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .background(shape = androidx.compose.foundation.shape.CircleShape, color = background)
    ) {
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.align(Alignment.Center),
            color = onBackground
        )
    }
}
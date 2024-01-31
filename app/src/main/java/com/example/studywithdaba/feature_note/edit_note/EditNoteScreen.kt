package com.example.studywithdaba.feature_note.edit_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.richtextfield.RichTextField
import com.example.richtextfield.RichTextFieldWithHint
import com.example.richtextfield.TextFormattingToolbar
import com.example.studywithdaba.core.data.util.toTimeDateString
import com.example.studywithdaba.core.design_system.component.BasicTextFieldWithHint
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(

    navController: NavController
) {
    val viewModel: EditNoteViewModel = hiltViewModel()
    val onEvent = viewModel::onEvent
    val state = viewModel.state.value
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(

            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
        ) {
            TopAppBar(
                title = { Text(text = "Edit note") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(EditNoteEvent.OnBack(navController)) }) {
                        Icon(SWDIcons.Back, null)
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(EditNoteEvent.OnTextToSpeech(context)) }, enabled = state.isTextToSpeechEnabled) {
                        Icon(SWDIcons.VolumeUp, null)
                    }
                }

            )
            Column(
                modifier = Modifier.padding(LocalDimensions.current.defaultPadding)
            ) {


                BasicTextFieldWithHint(
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    value = state.title,
                    onValueChange = {
                        onEvent(EditNoteEvent.OnTitleChange(it))
                    },
                    textStyle = MaterialTheme.typography.displayMedium.copy(color = MaterialTheme.colorScheme.primary),
                    hint = "Title ...",
                    hintStyle = MaterialTheme.typography.displayMedium.copy(
                        color = MaterialTheme.colorScheme.primary.copy(
                            0.5f
                        )
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                )
                Text(
                    text = state.date.toTimeDateString("dd/MM/yyyy HH:mm"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                RichTextFieldWithHint(
                    modifier = Modifier.fillMaxSize(),
                    richTextValue = state.richTextValue,
                    onValueChange = {
                        onEvent(EditNoteEvent.OnContentChange(it))
                    },
                    hint = "New note...",
                    hintStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface.copy(0.5f)),
                )
            }
        }
        TextFormattingToolbar(
            style = state.style,
            onStyleChange = { onEvent(EditNoteEvent.OnStyleChange(it))},
            colors = colors,
            defaultColor = MaterialTheme.colorScheme.onBackground,
            selectedColor = state.style.color,
            onDefaultColor = MaterialTheme.colorScheme.background,
            onColors = Color.Black,
            showColorPicker = state.showColorPicker,
            onShowColorPickerChange = {onEvent(EditNoteEvent.OnShowColorPickerChange(it))},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .imePadding()
        )
    }
}


internal val colors = listOf<Color>(
    Color(0xFFF56E30).copy(0.99f),
    Color(0xFFF5D430).copy(0.99f),
    Color(0xFFB0F530).copy(0.99f),
    Color(0xFF30F568).copy(0.99f),
    Color(0xFF30F5C7).copy(0.99f),
    Color(0xFF30D7F5).copy(0.99f),
    Color(0xFF30BDF5).copy(0.99f),
    Color(0xFF3730F5).copy(0.99f),
    Color(0xFFD130F5).copy(0.99f),
    Color(0xFFEE30F5).copy(0.99f),
    Color(0xffF5306B).copy(0.99f),
    Color(0xFFF53030).copy(0.99f),
)



package com.example.studywithdaba.feature_note.edit_note

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.studywithdaba.core.design_system.component.BasicTextFieldWithHint
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditNoteScreen(

    navController: NavController
) {
    val viewModel: EditNoteViewModel = hiltViewModel()
    val onEvent = viewModel::onEvent
    val state = viewModel.state.value
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(LocalDimensions.current.defaultPadding)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
        ) {
            IconButton(onClick = { onEvent(EditNoteEvent.OnBack(navController)) }) {
                Icon(SWDIcons.Back, null)
            }
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
            BasicTextFieldWithHint(
                modifier = Modifier.fillMaxSize(),
                value = state.content,
                onValueChange = {
                    onEvent(EditNoteEvent.OnContentChange(it))
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                hint = "New note ...",
                hintStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        0.5f
                    )
                ),
            )
        }
    }
}
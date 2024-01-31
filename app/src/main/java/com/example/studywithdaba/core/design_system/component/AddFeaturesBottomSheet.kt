package com.example.studywithdaba.core.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme

sealed class AddFeaturesBottomSheetEvent {
    object OnAddNote: AddFeaturesBottomSheetEvent()
    object OnAddFlashcards: AddFeaturesBottomSheetEvent()
    object OnAddDeck: AddFeaturesBottomSheetEvent()
    object OnDismiss: AddFeaturesBottomSheetEvent()
}
@Composable
fun AddFeaturesBottomSheet(
    onEvent: (AddFeaturesBottomSheetEvent) -> Unit,
) {
    SWDBottomSheet(onDismiss = { onEvent(AddFeaturesBottomSheetEvent.OnDismiss) }, title = "Add") {

        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(AddFeaturesBottomSheetEvent.OnAddNote)},
            headlineContent = { Text(text = "Add note") },
            leadingContent = { Icon(SWDIcons.NoteFilled, null)},
            trailingContent = { Icon(SWDIcons.Add, null)}
        )
        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(AddFeaturesBottomSheetEvent.OnAddFlashcards)},
            headlineContent = { Text(text = "Add flashcards") },
            leadingContent = { Icon(SWDIcons.FlashcardFilled, null)},
            trailingContent = { Icon(SWDIcons.Add, null)}
        )
        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(AddFeaturesBottomSheetEvent.OnAddDeck)},
            headlineContent = { Text(text = "Add flashcard deck") },
            leadingContent = { Icon(SWDIcons.Folder, null)},
            trailingContent = { Icon(SWDIcons.Add, null)}
        )
    }
}

@Preview
@Composable
fun AddFeaturesBottomSheetPreview() {
    StudyWithDabaTheme(
        darkTheme = true
    ) {
        fun onEvent(event: AddFeaturesBottomSheetEvent) {

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(LocalDimensions.current.defaultPadding)
                .navigationBarsPadding()
        ) {
            AddFeaturesBottomSheet(onEvent = {onEvent(it)})
        }
    }
}
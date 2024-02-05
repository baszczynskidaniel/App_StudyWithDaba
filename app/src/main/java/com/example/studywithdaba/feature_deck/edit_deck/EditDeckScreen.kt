package com.example.studywithdaba.feature_deck.edit_deck

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.studywithdaba.feature_deck.add_deck.AddEditDeckScreen


@Composable
fun EditDeckScreen(
    state: EditDeckState,
    onEvent: (EditDeckEvent) -> Unit,
    navController: NavController
) {
    AddEditDeckScreen(
        topBarLabel = "Edit deck",
        confirmButtonText = "Apply changes",
        title = state.title,
        description = state.description,
        titleError = state.titleError,
        favourite = state.favourite,
        onBack = { onEvent(EditDeckEvent.OnBack(navController)) },
        onFavouriteChange = { onEvent(EditDeckEvent.OnFavouriteChange(it))},
        onTitleChange = {onEvent(EditDeckEvent.OnTitleChange(it))},
        onDescriptionChange = {onEvent(EditDeckEvent.OnDescriptionChange(it))},
        onTitleClear = {onEvent(EditDeckEvent.OnTitleClear)},
        onDescriptionClear = {onEvent(EditDeckEvent.OnDescriptionClear)},
        onAddDeck = {onEvent(EditDeckEvent.OnApplyChanges(navController))}
    )
}



package com.example.studywithdaba.feature_deck.add_deck

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavController
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions



@Composable
fun AddEditDeckScreen(
    state: AddDeckState,
    onEvent: (AddDeckEvent) -> Unit,
    navController: NavController
) {
    AddEditDeckScreen(
        topBarLabel = "Add deck",
        confirmButtonText = "Add deck",
        title = state.title,
        description = state.description,
        titleError = state.titleError,
        favourite = state.favourite,
        onBack = { onEvent(AddDeckEvent.OnBack(navController)) },
        onFavouriteChange = { onEvent(AddDeckEvent.OnFavouriteChange(it))},
        onTitleChange = {onEvent(AddDeckEvent.OnTitleChange(it))},
        onDescriptionChange = {onEvent(AddDeckEvent.OnDescriptionChange(it))},
        onTitleClear = {onEvent(AddDeckEvent.OnTitleClear)},
        onDescriptionClear = {onEvent(AddDeckEvent.OnDescriptionClear)},
        onAddDeck = {onEvent(AddDeckEvent.OnAddDeck(navController))}
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddEditDeckScreen(
    topBarLabel: String,
    confirmButtonText: String,
    title: String,
    description: String,
    titleError: String?,
    favourite: Boolean,
    onBack: () -> Unit,
    onFavouriteChange: (Boolean) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTitleClear: () -> Unit,
    onDescriptionClear: () -> Unit,
    onAddDeck: () -> Unit,
) {
    val (descriptionFocus) = remember {
        FocusRequester.createRefs()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = LocalDimensions.current.defaultPadding,
                bottom = LocalDimensions.current.defaultPadding,
                end = LocalDimensions.current.defaultPadding,
            )
            .navigationBarsPadding()
        ,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            CenterAlignedTopAppBar(
                title = { Text(topBarLabel) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(imageVector = SWDIcons.Back, contentDescription = null)
                    }
                 },

                actions = {
                    IconButton(onClick = { onFavouriteChange(!favourite) }) {
                        if(favourite)
                            Icon(imageVector = SWDIcons.FavouriteFilled, contentDescription = null)
                        else
                            Icon(SWDIcons.FavouriteOutlined, null)
                    }
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if(title.isNotBlank()) {
                        IconButton(onClick = { onTitleClear() }) {
                            Icon(SWDIcons.Clear, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                maxLines = 1,
                isError = titleError != null,
                supportingText = {
                    if(titleError != null)
                        Text(text = titleError)
                    else
                        Text("*required")
                },
                placeholder = {
                    Text("Title")
                },
                value = title,
                singleLine = true,
                onValueChange = { onTitleChange(it) },
                label = { Text("Title")},
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        descriptionFocus.requestFocus()
                    }
                ),
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocus)
                ,
                trailingIcon = {
                    if(description.isNotBlank()) {
                        IconButton(onClick = { onDescriptionClear() }) {
                            Icon(SWDIcons.Clear, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                placeholder = {
                    Text("Description")
                },
                value = description,
                onValueChange = { onDescriptionChange(it) },
                label = { Text("description")}
            )
        }
        Button(onClick = { onAddDeck() }, modifier = Modifier.fillMaxWidth() ) {
            Text(text = confirmButtonText)
        }
    }
}

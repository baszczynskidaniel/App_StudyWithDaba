package com.example.studywithdaba.feature_deck.add_deck

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.studywithdaba.MainActivity
import com.example.studywithdaba.core.data.repository.DeckRepository
import com.example.studywithdaba.core.data.util.ValidationResult
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class AddEditDeckViewModel @AssistedInject constructor(
    private val deckRepository: DeckRepository,
    @Assisted private val deckId: Long?
): ViewModel() {
    private val validateTitle: ValidateTitle = ValidateTitle()
    private val _state = MutableStateFlow(AddEditDeckState())
    private val _validationEvent = Channel<ValidationEvent>()
    val validationEvent = _validationEvent.receiveAsFlow()
    val state = _state

    init {
        if(deckId != null) {
            viewModelScope.launch {
                val deck = deckRepository.getDeckById(deckId)
                _state.update {it.copy(
                    title = deck!!.title,
                    description = deck.description,
                    label = "Edit deck"
                ) }
            }
        } else {
            _state.update { it.copy(
                label = "Add deck"
            )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(deckId: Long?): AddEditDeckViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            deckId: Long?
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(deckId) as T
            }
        }
    }


    fun onEvent(event: AddEditDeckEvent) {
        when(event) {
            AddEditDeckEvent.OnAddEditDeck -> {
                val titleResult = validateTitle.execute(_state.value.title)
                if(titleResult.successful) {
                    viewModelScope.launch {

                        val deck =  if(deckId == null)
                            Deck(_state.value.title, _state.value.description)
                        else
                            Deck(deckId = deckId, title = _state.value.title, description =  _state.value.description)
                        deckRepository.insertDeck(deck)
                        _validationEvent.send(ValidationEvent.Success(_state.value.title))
                    }
                } else {
                    _state.update { it.copy(
                        titleError = titleResult.errorMessage
                    ) }
                }
            }
            is AddEditDeckEvent.OnDescriptionChange -> { _state.update {it.copy(
                description = event.descriptionChange
                ) }
            }
            AddEditDeckEvent.OnDescriptionClear ->  { _state.update {it.copy(
                description = ""
            ) }
            }
            is AddEditDeckEvent.OnTitleChange ->  { _state.update {it.copy(
                title = event.titleChange,
                titleError = null
            ) }
            }
            AddEditDeckEvent.OnTitleClear ->  { _state.update {it.copy(
                title = ""
            ) }
            }
        }
    }

    sealed class ValidationEvent {
        data class Success(val deckName: String): ValidationEvent()
    }
}

@Composable
fun addEditDeckViewModel(deckId: Long?): AddEditDeckViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).addEditDeckViewModel()

    return viewModel(factory = AddEditDeckViewModel.provideFactory(factory, deckId))
}



class ValidateTitle {
    fun execute(title: String): ValidationResult {
        if(title.isBlank()) {
            return ValidationResult(
                false,
                "The title cannot be blank"
            )
        } else {
            return ValidationResult(
                true
            )
        }
    }
}

data class AddEditDeckState(
    val title: String = "",
    val description: String = "",
    val titleError: String? = null,
    val label: String = ""
)

sealed class AddEditDeckEvent {
    object OnTitleClear: AddEditDeckEvent()
    object OnDescriptionClear: AddEditDeckEvent()
    data class OnTitleChange(val titleChange: String): AddEditDeckEvent()
    data class OnDescriptionChange(val descriptionChange: String): AddEditDeckEvent()
    object OnAddEditDeck: AddEditDeckEvent()
}

@Composable
fun AddDeckDialog(
    onDismiss: () -> Unit,
    state: AddEditDeckState,
    onEvent: (AddEditDeckEvent) -> Unit,
) {
    AddDeckDialog(
        title = state.title,
        description = state.description,
        onDismiss = { onDismiss() },
        onTitleChange =  { onEvent(AddEditDeckEvent.OnTitleChange(it)) },
        onDescriptionChange =  { onEvent(AddEditDeckEvent.OnDescriptionChange(it)) },
        onAddDeck =  { onEvent(AddEditDeckEvent.OnAddEditDeck) },
        onTitleClear =  { onEvent(AddEditDeckEvent.OnTitleClear) },
        onDescriptionClear =  { onEvent(AddEditDeckEvent.OnDescriptionClear) },
        titleError = state.titleError,
        label = state.label
    )
}

@Composable
fun AddDeckDialog(
    title: String,
    label: String,
    description: String,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddDeck: () -> Unit,
    onTitleClear: () -> Unit,
    onDescriptionClear: () -> Unit,
    titleError: String?
) {
    val focusManager = LocalFocusManager.current
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
    ) {

        Card(
            modifier = Modifier
                .width(LocalDimensions.current.alertDialogWidth)
                .padding(LocalDimensions.current.doubleDefaultPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalDimensions.current.defaultPadding),
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                TextField(
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                          Text("Title")
                    },
                    isError = titleError != null,
                    supportingText = {
                        if(titleError != null)
                             Text(text = titleError)
                        else
                            Text("*required")
                    },
                    value = title,
                    onValueChange = {onTitleChange(it)},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    trailingIcon = {
                        if(title.isNotBlank()) {
                            IconButton(onClick = { onTitleClear() }) {
                                Icon(SWDIcons.Clear, null)
                            }
                        }
                    },
                    singleLine = true,
                    label = {
                        Text(text = "Title")
                    }
                )

                TextField(
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Description")
                    },
                    value = description,
                    onValueChange = {onDescriptionChange(it)},

                    trailingIcon = {
                        if(description.isNotBlank()) {
                            IconButton(onClick = { onDescriptionClear() }) {
                                Icon(SWDIcons.Clear, null)
                            }
                        }
                    },
                    label = {
                        Text(text = "Description")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = { onAddDeck() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(text = "Apply")
                    }
                }
            }
        }
    }
}


//@Preview
//@Composable
//internal fun AddDeckDialogPreview() {
//    var showDialog by remember {
//        mutableStateOf(false)
//    }
//    StudyWithDabaTheme(darkTheme = true) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background)
//                .padding(LocalDimensions.current.defaultPadding)
//        ) {
//            Button(onClick = { showDialog = true }) {
//                Text(text = "Show dialog")
//            }
//            if(showDialog) {
//                val viewModel = viewModel<AddDeckViewModel>()
//                val state = viewModel.state.collectAsState()
//                AddDeckDialog(
//                    onDismiss = { showDialog = false },
//                    state = state.value,
//                    onEvent = viewModel::onEvent
//                )
//                val context = LocalContext.current
//                LaunchedEffect(key1 = context) {
//                    viewModel.validationEvent.collect {event ->
//                        Log.d("launched", "tu")
//                        when(event) {
//                            is AddDeckViewModel.ValidationEvent.Success -> {
//                                Toast.makeText(
//                                    context,
//                                    "Added ${event.deckName}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                showDialog = false
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
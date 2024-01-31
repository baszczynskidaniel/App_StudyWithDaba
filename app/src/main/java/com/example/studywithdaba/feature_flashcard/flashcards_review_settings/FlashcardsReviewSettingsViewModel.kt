package com.example.studywithdaba.feature_flashcard.flashcards_review_settings

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studywithdaba.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

class FlashcardsReviewSettingsViewModel @AssistedInject constructor(
    @Assisted val initState: FlashcardsReviewSettingsState
): ViewModel() {
    private val _state = mutableStateOf(initState)
    val state: State<FlashcardsReviewSettingsState> = _state

    fun onEvent(
        event: FlashcardsReviewSettingsEvent
    ) {
        when(event) {
            is FlashcardsReviewSettingsEvent.OnFavouriteChange -> onFavouriteChange(event.value)
            is FlashcardsReviewSettingsEvent.OnInfinityModeChange -> onInfinityModeChange(event.value)
            is FlashcardsReviewSettingsEvent.OnShuffleChange -> onShuffleChange(event.value)
            is FlashcardsReviewSettingsEvent.OnVisibilityChange -> onVisibilityChange(event.visibility)
        }
    }
    private fun onFavouriteChange(value: Boolean) {
        _state.value = _state.value.copy(
            onlyFavourite = value
        )
    }
    private fun onShuffleChange(value: Boolean) {
        _state.value = _state.value.copy(
            shuffle = value
        )
    }
    private fun onInfinityModeChange(value: Boolean) {
        _state.value = _state.value.copy(
            infiniteMode = value
        )
    }

    private fun onVisibilityChange(visibility: FlashcardRepeatedVisibility) {
        _state.value = _state.value.copy(
            visibility = visibility
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(initState: FlashcardsReviewSettingsState): FlashcardsReviewSettingsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            initState: FlashcardsReviewSettingsState
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(initState) as T
            }
        }
    }
}

@Composable
fun flashcardsReviewSettingsViewModel(initState: FlashcardsReviewSettingsState): FlashcardsReviewSettingsViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).flashcardsReviewSettingsViewModelFactory()

    return viewModel(factory = FlashcardsReviewSettingsViewModel.provideFactory(factory, initState))
}
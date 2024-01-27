package com.example.studywithdaba.core.design_system.component

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme


data class SWDSearchBarState(
    val query: String = "",
    val isActive: Boolean = false,
    val history: List<String> = emptyList<String>(),
    val showHistory: Boolean = true,
)

sealed class SWDSearchBarEvent {
    data class OnQueryChange(val queryChange: String): SWDSearchBarEvent()
    data class OnSearch(val query: String): SWDSearchBarEvent()
    data class OnActiveChange(val activeChange: Boolean): SWDSearchBarEvent()
    object OnHistoryClear: SWDSearchBarEvent()
    data class OnHistoryClick(val historyQuery: String, val historyIndex: Int): SWDSearchBarEvent()
    data class OnRemoveHistory(val historyQuery: String, val historyIndex: Int): SWDSearchBarEvent()
    object OnBack: SWDSearchBarEvent()
    object OnQueryClear: SWDSearchBarEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SWDSearchBar(
    modifier: Modifier = Modifier,
    placeholder: String,
    state: SWDSearchBarState,
    onEvent: (SWDSearchBarEvent) -> Unit,
    contentColor: Color,
) {
    val containerColorState: Color by animateColorAsState(
        targetValue =
        if(state.isActive) MaterialTheme.colorScheme.background
        else Color.White, label = "container color animation"
    )
    var contentColorState by remember {
        mutableStateOf(contentColor)
    }
    contentColorState = if(state.isActive) contentColor else Color.Black

    SearchBar(
        modifier = if(state.isActive) Modifier else modifier,
        colors = SearchBarDefaults.colors(
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = contentColorState,
                unfocusedTextColor = contentColorState,
            ),
            containerColor = containerColorState,
        ),
        placeholder = {
            Text(text = placeholder, color = contentColorState.copy(0.65f))
        },
        leadingIcon = {
            if(state.isActive)
                IconButton(onClick = { onEvent(SWDSearchBarEvent.OnBack) }) {
                    Icon(SWDIcons.Back, null, tint = contentColorState)
                }
            else
                IconButton(onClick = { onEvent(SWDSearchBarEvent.OnActiveChange(true)) }) {
                    Icon(SWDIcons.Search, null, tint = contentColorState)
                }
        },
        trailingIcon = {
            if(state.query.isNotEmpty())
                IconButton(onClick = { onEvent(SWDSearchBarEvent.OnQueryClear) }) {
                    Icon(SWDIcons.Clear, null, tint = contentColorState)
                }
        },

        query = state.query,
        onQueryChange = { onEvent(SWDSearchBarEvent.OnQueryChange(it))},
        onSearch = { onEvent(SWDSearchBarEvent.OnSearch(it))},
        active = state.isActive,
        onActiveChange = { onEvent(SWDSearchBarEvent.OnActiveChange(!it))},
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            if(state.showHistory) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent searches",
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { onEvent(SWDSearchBarEvent.OnHistoryClear) }) {
                        Text(text = "Clear")
                        Icon(imageVector = SWDIcons.DeleteOutlined, contentDescription = null, tint = contentColorState)
                    }
                }
                state.history.forEachIndexed() { index, historyQuery ->
                    ListItem(
                        headlineContent = { Text(text = historyQuery, color = contentColorState) },
                        modifier = Modifier.clickable {
                            onEvent(SWDSearchBarEvent.OnHistoryClick(historyQuery, index))
                        },
                        leadingContent = {
                            Icon(imageVector = SWDIcons.History, contentDescription = null, tint = contentColorState)
                        },
                        trailingContent = {
                            IconButton(onClick = {
                                onEvent(
                                    SWDSearchBarEvent.OnRemoveHistory(historyQuery, index)
                                )
                            }) {
                                Icon(imageVector = SWDIcons.Clear, contentDescription = null, tint = contentColorState)
                            }
                        }
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//internal fun SWDSearchBarPreview(
//) {
//    var state by remember {
//        mutableStateOf(SWDSearchBarState())
//    }
//
//    fun onEvent(event: SWDSearchBarEvent) {
//        when(event) {
//            is SWDSearchBarEvent.OnActiveChange -> {
//                state = state.copy(
//                    isActive = !event.activeChange
//                )
//            }
//            SWDSearchBarEvent.OnHistoryClear -> {
//                state = state.copy(
//                    history = emptyList()
//                )
//            }
//            is SWDSearchBarEvent.OnHistoryClick -> state = state.copy(
//                isActive = false
//            )
//            is SWDSearchBarEvent.OnQueryChange -> state = state.copy(
//                query = event.queryChange,
//                showHistory = event.queryChange.isBlank()
//            )
//            is SWDSearchBarEvent.OnRemoveHistory -> {
//                var newHistory = state.history.toMutableSet()
//                newHistory.remove(event.historyQuery)
//                state = state.copy(
//                    history = newHistory
//                )
//            }
//            is SWDSearchBarEvent.OnSearch -> {
//                var newHistory = state.history.toMutableSet()
//                newHistory.add(event.query)
//                state = state.copy(
//                    isActive = false,
//                    history = newHistory
//
//                )
//            }
//
//            SWDSearchBarEvent.OnBack -> state = state.copy(
//                query = "",
//                isActive = false
//            )
//            SWDSearchBarEvent.OnQueryClear -> state = state.copy(
//                query = "",
//                showHistory = true
//            )
//        }
//    }
//    val contextForToast = LocalContext.current.applicationContext
//    StudyWithDabaTheme {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//            SWDSearchBar(
//                modifier = Modifier.padding(LocalDimensions.current.defaultPadding),
//                placeholder = "search",
//                state = state,
//                onEvent = { onEvent(it) },
//                contentColor = MaterialTheme.colorScheme.onSurface
//            )
//            LaunchedEffect(!state.isActive) {
//                Toast.makeText(
//                    contextForToast,
//                    state.query,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//}
//

package com.example.studywithdaba.feature_note

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Note
import com.example.studywithdaba.core.design_system.component.AddFeaturesBottomSheetEvent
import com.example.studywithdaba.core.design_system.component.BlackWhiteChip
import com.example.studywithdaba.core.design_system.component.DialogSelectorWithState
import com.example.studywithdaba.core.design_system.component.SWDBottomSheet
import com.example.studywithdaba.core.design_system.component.SWDSearchBar
import com.example.studywithdaba.core.design_system.component.SWDSearchBarEvent
import com.example.studywithdaba.core.design_system.component.SWDSearchBarState
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme


internal enum class TopBarVisibility {
    NONE,
    SELECTION_BAR,
    DEFAULT_BAR
}

enum class NotesSortBy {
    DEFAULT,
    DATE,
    TITLE,
    CONTENT;

    override fun toString(): String {
        return when(this) {
            DEFAULT -> "Default"
            DATE -> "Date"
            TITLE -> "Title"
            CONTENT -> "Content"
        }
    }
    fun getLabelMessage(): String {
        return "Sort by" + when(this) {
            DEFAULT -> ""
            DATE -> ": date"
            TITLE -> ": title"
            CONTENT -> ": content"
        }
    }
}

enum class NotesFilterBy {
    DEFAULT,
    ONLY_FAVOURITE;

    override fun toString(): String {
        return when(this) {
            DEFAULT -> "None"
            ONLY_FAVOURITE -> "only favourite"
        }
    }
    fun getLabelMessage(): String {
        return when(this) {
            DEFAULT -> "filter by"
            ONLY_FAVOURITE -> "filter by: favourite"
        }
    }
 }

data class NotesState(
    val areAllSelectedNotesFavourite: Boolean = false,
    val gridSize: Int = 2,
    val areNotesInInvertedOrder: Boolean = false,
    val searchState: SWDSearchBarState = SWDSearchBarState(),
    val sortBy: NotesSortBy = NotesSortBy.DEFAULT,
    val filterBy: NotesFilterBy = NotesFilterBy.DEFAULT,
    val showSortByDialog: Boolean = false,
    val showFilterByDialog: Boolean = false,
    val notes: List<Note> = emptyList(),
    val selectedNotesId: Set<Long> = emptySet(),
    val showNoteBottomSheet: Boolean = false,
    val bottomSheetNoteId: Long = -1L,
)

sealed class NotesEvent {
    data class OnNoteBottomSheetEvent(val event: NoteBottomSheetEvent): NotesEvent()
    object OnSettings: NotesEvent()
    object OnClearSelection : NotesEvent()
    object OnRemoveSelectedNotes: NotesEvent()
    data class OnSelectionFavouriteChange(val oldValue: Boolean): NotesEvent()
    data class OnGridSizeChange(val oldValue: Int): NotesEvent()
    data class OnSearchEvent(val event: SWDSearchBarEvent): NotesEvent()
    data class OnInvert(val invertChange: Boolean): NotesEvent()
    data class OnSortByDismiss(val sortByChange: NotesSortBy?): NotesEvent()
    data class OnFilterByDismiss(val filterByChange: NotesFilterBy?): NotesEvent()
    object OnSortByClick: NotesEvent()
    object OnFilterByClick: NotesEvent()
    data class OnAddNote(val navController: NavController): NotesEvent()
    data class OnMoreNoteClick(val noteId: Long): NotesEvent()
    object OnReset: NotesEvent()
    data class OnNoteItemClick(val noteId: Long, val navController: NavController): NotesEvent()
    data class OnNoteItemLongClick(val noteId: Long, val selectedChange: Boolean): NotesEvent()
    data class OnNoteItemFavouriteClick(val noteId: Long, val favouriteChange: Boolean): NotesEvent()

}

sealed class NoteBottomSheetEvent {
    object OnDismiss: NoteBottomSheetEvent()
    data class OnRemove(val noteId: Long): NoteBottomSheetEvent()
    data class OnGenerateFlashcards(val noteId: Long, val context: Context): NoteBottomSheetEvent()
}
@Composable
fun NoteBottomSheet(
    noteId: Long,
    onEvent: (NoteBottomSheetEvent) -> Unit
) {
    val context = LocalContext.current
    SWDBottomSheet(onDismiss = { onEvent(NoteBottomSheetEvent.OnDismiss) }, title = "Note option") {
        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(NoteBottomSheetEvent.OnGenerateFlashcards(noteId, context)) },
            headlineContent = { Text(text = "Generate flashcards with ai based on note") },
            leadingContent = {
                Icon(SWDIcons.Robot, null)
            }
        )
        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(NoteBottomSheetEvent.OnRemove(noteId)) },
            headlineContent = { Text(text = "Remove flashcard") },
            leadingContent = {
                Icon(SWDIcons.DeleteOutlined, null)
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    state: NotesState,
    onEvent: (NotesEvent) -> Unit,
    innerPaddingValues: PaddingValues = PaddingValues(),
    navController: NavController
) {
    var topBarVisibility by remember {
        mutableStateOf(TopBarVisibility.DEFAULT_BAR)
    }
    topBarVisibility = when {
        state.searchState.isActive -> TopBarVisibility.NONE
        state.selectedNotesId.isNotEmpty() -> TopBarVisibility.SELECTION_BAR
        else -> TopBarVisibility.DEFAULT_BAR
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPaddingValues.calculateBottomPadding())
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomEnd = LocalDimensions.current.bigClip,
                            bottomStart = LocalDimensions.current.bigClip
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                when (topBarVisibility) {
                    TopBarVisibility.NONE -> {}
                    TopBarVisibility.SELECTION_BAR -> SelectionTopBar(
                        numberOfSelectedNotes = state.selectedNotesId.size,
                        onClear = { onEvent(NotesEvent.OnClearSelection) },
                        onRemove = { onEvent(NotesEvent.OnRemoveSelectedNotes) },
                        onFavourite = { onEvent(NotesEvent.OnSelectionFavouriteChange(it)) },
                        areAllSelectedNotesFavourite = state.areAllSelectedNotesFavourite
                    )

                    else -> {}

//                TopBarVisibility.DEFAULT_BAR -> MainTopBar(
//                    gridSize = state.gridSize,
//                    onGridSizeChange = { onEvent(NotesEvent.OnGridSizeChange(it)) },
//                    onSettings = { onEvent(NotesEvent.OnSettings) })
                }
                SWDSearchBar(
                    modifier = Modifier
                        .padding(horizontal = LocalDimensions.current.defaultPadding)
                        .align(Alignment.CenterHorizontally),
                    placeholder = "Search in notes",
                    state = state.searchState,
                    onEvent = { onEvent(NotesEvent.OnSearchEvent(it)) },
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
                //Spacer(modifier = Modifier.height(LocalDimensions.current.halfDefaultPadding))
                LazyRow(
                    modifier = Modifier.padding(horizontal = LocalDimensions.current.defaultPadding),
                    horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding)
                ) {
                    item() {
                        BlackWhiteChip(
                            selected = false,
                            onClick = { onEvent(NotesEvent.OnGridSizeChange(state.gridSize)) },
                            label = {
                                if (state.gridSize == 1)
                                    Icon(SWDIcons.Column2, null)
                                else
                                    Icon(SWDIcons.Column1, null)
                            },

                            )
                    }
                    item() {
                        BlackWhiteChip(
                            selected = state.filterBy != NotesFilterBy.DEFAULT,
                            onClick = { onEvent(NotesEvent.OnFilterByClick) },
                            label = { Text(text = state.filterBy.getLabelMessage()) },
                            trailingIcon = { Icon(SWDIcons.ExpandMote, null) }
                        )
                    }
                    item() {
                        BlackWhiteChip(
                            selected = state.sortBy != NotesSortBy.DEFAULT,
                            onClick = { onEvent(NotesEvent.OnSortByClick) },
                            label = { Text(text = state.sortBy.getLabelMessage()) },
                            trailingIcon = { Icon(SWDIcons.ExpandMote, null) }
                        )
                    }
                    item() {
                        BlackWhiteChip(
                            selected = state.areNotesInInvertedOrder,
                            onClick = { onEvent(NotesEvent.OnInvert(state.areNotesInInvertedOrder)) },
                            label = { Icon(SWDIcons.Switch, null) },
                            trailingIcon = {}
                        )
                    }
                }
                Spacer(modifier = Modifier.height(LocalDimensions.current.halfDefaultPadding))

            }
//        if(state.filterBy != NotesFilterBy.DEFAULT || state.sortBy != NotesSortBy.DEFAULT || state.areNotesInInvertedOrder || state.)
            FilterResultRow(
                modifier = Modifier.padding(horizontal = LocalDimensions.current.defaultPadding),
                result = state.notes.size,
                onReset = { onEvent(NotesEvent.OnReset) }
            )
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(state.gridSize),
                contentPadding = PaddingValues(horizontal = LocalDimensions.current.defaultPadding),
                verticalItemSpacing = LocalDimensions.current.defaultPadding,
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
            ) {
                items(state.notes.size) { index ->
                    NoteSummaryItem(
                        note = state.notes[index],
                        selected = state.notes[index].noteId in state.selectedNotesId,
                        onClick = { onEvent(NotesEvent.OnNoteItemClick(it, navController)) },
                        onLongClick = { noteId, selectedChange ->
                            onEvent(NotesEvent.OnNoteItemLongClick(noteId, selectedChange))
                        },
                        onFavouriteClick = { noteId, favouriteChange ->
                            onEvent(NotesEvent.OnNoteItemFavouriteClick(noteId, favouriteChange))
                        },
                        onMoreClick = {
                            onEvent(NotesEvent.OnMoreNoteClick(it))
                        }
                    )
                }
            }
        }
        FloatingActionButton(onClick = { onEvent(NotesEvent.OnAddNote(navController)) }, modifier = Modifier
            .padding(
                LocalDimensions.current.defaultPadding
            )
            .align(Alignment.BottomEnd)) {
            Icon(imageVector = SWDIcons.Add, contentDescription = null)

        }
    }

    if(state.showSortByDialog)
        DialogSelectorWithState(
            initialValue = state.sortBy,
            label = state.sortBy.toString(),
            values = NotesSortBy.values(),
            onDismiss = {
                onEvent(NotesEvent.OnSortByDismiss(it))
            }
        )
    if(state.showFilterByDialog)
        DialogSelectorWithState(
            initialValue = state.filterBy,
            label = state.filterBy.toString(),
            values = NotesFilterBy.values(),
            onDismiss = {
                onEvent(NotesEvent.OnFilterByDismiss(it))
            }
        )
    if(state.showNoteBottomSheet) {
        NoteBottomSheet(noteId = state.bottomSheetNoteId, onEvent = {onEvent(NotesEvent.OnNoteBottomSheetEvent(it))})
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopBar(
    gridSize: Int,
    onGridSizeChange: (Int) -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var gridIcon by remember {
        mutableStateOf(SWDIcons.Column1)
    }
    gridIcon = if(gridSize == 1) SWDIcons.Column2 else SWDIcons.Column1

    TopAppBar(
        title = { Text(text = "Notes")},
        actions = {
            IconButton(onClick = { onGridSizeChange(gridSize) }) {
                Icon(imageVector = gridIcon, contentDescription = null)
            }
            IconButton(onClick = { onSettings() }) {
                Icon(imageVector = SWDIcons.SettingsFilled, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectionTopBar(
    numberOfSelectedNotes: Int,
    onClear: () -> Unit,
    onRemove: () -> Unit,
    onFavourite: (Boolean) -> Unit,
    areAllSelectedNotesFavourite: Boolean,
) {
    TopAppBar(
        title = { Text(text = "$numberOfSelectedNotes")},
        actions = {
            IconButton(onClick = { onClear() }) {
                Icon(imageVector = SWDIcons.Clear, contentDescription = null)
            }
            IconToggleButton(checked = areAllSelectedNotesFavourite, onCheckedChange = { onFavourite(areAllSelectedNotesFavourite) }) {
                if(areAllSelectedNotesFavourite)
                    Icon(imageVector = SWDIcons.FavouriteFilled, contentDescription = null)
                else
                    Icon(imageVector = SWDIcons.FavouriteOutlined, contentDescription = null)
            }
            IconButton(onClick = { onRemove() }) {
                Icon(imageVector = SWDIcons.DeleteOutlined, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteOptionsBottomSheet(
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() }

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            ListItem(
                leadingContent = { Icon(SWDIcons.DeleteOutlined, null) },
                headlineContent = {
                    Text(text = "Remove note", color = MaterialTheme.colorScheme.onSurface)
                },
                modifier = Modifier.clickable { onDismissRequest() }
            )
            ListItem(
                leadingContent = { Icon(SWDIcons.Robot, null) },
                headlineContent = {
                    Text(
                        text = "Generate flashcards with AI",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier.clickable { onDismissRequest() }
            )
        }
    }
}

@Composable
fun FilterResultRow(
    result: Int,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
           Text(text = "$result - results", color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = { onReset() }) {
                Text(text = "Reset")
            }
        }
    }
}


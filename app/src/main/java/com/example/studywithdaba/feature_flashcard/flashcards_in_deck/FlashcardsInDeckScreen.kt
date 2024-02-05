package com.example.studywithdaba.feature_flashcard.flashcards_in_deck

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Flashcard
import com.example.studywithdaba.core.design_system.component.SWDBottomSheet
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions


@Composable
fun FlashcardsInDeckScreen(
    state: FlashcardsInDeckState,
    onEvent: (FlashcardsInDeckEvent) -> Unit,
    navController: NavController
) {
    FlashcardsInDeckScreen(
        navController = navController,
        showFlashcardBottomSheet = state.showFlashcardBottomSheet,
        bottomSheetFlashcardId = state.bottomSheetFlashcardId,
        deckTitle = state.deckTitle,
        visibleBackFlashcardIds = state.visibleBackFlashcardIds,
        flashcards = state.flashcards,
        numberOfFlashcardToRepeat = state.numberOfFlashcardToRepeat,
        onOneSideFlashcardClick = { onEvent(FlashcardsInDeckEvent.OnOneSideFlashcardClick(it))},
        onFavouriteFlashcard = { id, favouriteChange -> onEvent(FlashcardsInDeckEvent.OnFavouriteFlashcard(id, favouriteChange))},
        onBack = { onEvent(FlashcardsInDeckEvent.OnBack(navController)) },
        onRepeat = { onEvent(FlashcardsInDeckEvent.OnRepeat(navController)) },
        onQuiz = { onEvent(FlashcardsInDeckEvent.OnQuiz(navController)) },
        onReview = { onEvent(FlashcardsInDeckEvent.OnReview(navController)) },
        onAddFlashcard = { onEvent(FlashcardsInDeckEvent.OnAddFlashcard(navController)) },
        onMenuFlashcard = { onEvent(FlashcardsInDeckEvent.OnMenuFlashcard(it))},
        onFlashcardBottomSheetEvent = { onEvent(FlashcardsInDeckEvent.OnFlashcardBottomSheetEvent(it))}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsInDeckScreen(
    navController: NavController,
    showFlashcardBottomSheet: Boolean,
    bottomSheetFlashcardId: Long,
    deckTitle: String,
    visibleBackFlashcardIds: Set<Long>,
    flashcards: List<Flashcard>,
    numberOfFlashcardToRepeat: Int,
    onOneSideFlashcardClick: (Long) -> Unit,
    onFavouriteFlashcard: (Long, Boolean) -> Unit,
    onBack: () -> Unit,
    onRepeat: () -> Unit,
    onQuiz: () -> Unit,
    onReview: () -> Unit,
    onAddFlashcard: () -> Unit,
    onMenuFlashcard: (Long) -> Unit,

    onFlashcardBottomSheetEvent: (FlashcardBottomSheetEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = LocalDimensions.current.defaultPadding,
                bottom = LocalDimensions.current.defaultPadding,
                end = LocalDimensions.current.defaultPadding,
            )
            .navigationBarsPadding()
    ) {
        TopAppBar(title = {
            Text(text = deckTitle)},
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(SWDIcons.Back, null)
                }
            }
        )
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = LocalDimensions.current.defaultPadding,
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
            modifier = Modifier.fillMaxWidth()
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                OneSideFlashcardView(
                    flashcards = flashcards,
                    visibleBackFlashcardIds = visibleBackFlashcardIds,
                    onClick = {
                        onOneSideFlashcardClick(it)
                    },
                    onFavouriteClick = { id, favouriteChange ->
                        onFavouriteFlashcard(id, favouriteChange)
                    },
                    onMenuClick = {
                        onMenuFlashcard(it)
                    },
                    onAddFlashcard = {
                        onAddFlashcard()
                    }
                )
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                FlashcardNavigationMenu(
                    numberOfFlashcardToRepeat = numberOfFlashcardToRepeat,
                    onAdd = { onAddFlashcard() },
                    onRepeat = { onRepeat() },
                    onQuiz = { onQuiz() },
                    onFlashcards = { onReview() },
                )
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                Text(text = "Flashcards", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            }
            items(flashcards.size)
            { index ->
                FlashcardSummaryItem(
                    flashcard = flashcards[index],
                    onFavouriteClick = {
                        onFavouriteFlashcard(flashcards[index].flashcardId, !flashcards[index].favourite)
                    },
                    onClick = {  },
                    onLongClick = {   },
                    onMenuClick = { onMenuFlashcard(flashcards[index].flashcardId) },
                    selected = false,
                )
            }
        }
    }
    if(showFlashcardBottomSheet) {
        FlashcardBottomSheet(onEvent = { onFlashcardBottomSheetEvent(it)}, noteId = bottomSheetFlashcardId, navController = navController)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlashcardSummaryItem(
    modifier: Modifier = Modifier,
    flashcard: Flashcard,
    onFavouriteClick: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onMenuClick: () -> Unit,
    selected: Boolean,
) {
    Card(
        modifier = Modifier
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        border = if(selected) BorderStroke(width = LocalDimensions.current.highlightWidth, MaterialTheme.colorScheme.primary) else null,
    ) {
        Column (
            modifier = Modifier
                .padding(LocalDimensions.current.defaultPadding)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconToggleButton(
                    checked = flashcard.favourite,
                    onCheckedChange = {
                        onFavouriteClick()
                    }
                ) {
                    if (flashcard.favourite) {
                        Icon(SWDIcons.FavouriteFilled, null, tint = MaterialTheme.colorScheme.primary)
                    } else {
                        Icon(SWDIcons.FavouriteOutlined, null)
                    }
                }
                IconButton(onClick = { onMenuClick() }) {
                    Icon(SWDIcons.DotMenu, null)
                }
            }
            Text(
                text = flashcard.front,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = flashcard.back,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun HorizontalFlashcardItem(
    isFrontVisible: Boolean,
    flashcard: Flashcard,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
    onFavouriteClick: () -> Unit,
) {
    val rotation by animateFloatAsState(targetValue = if(isFrontVisible) 0f else 180f, animationSpec = tween(500),
        label = "Flipping flashcard animation"
    )
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = Modifier
            .clickable(
                onClick = { onClick() }
            )
            .graphicsLayer {
                rotationX = rotation
                cameraDistance = 20 * density
            }
    ) {
        Column(
            modifier = Modifier
                .size(width = 320.dp, height = 240.dp)
                .padding(LocalDimensions.current.defaultPadding),
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (rotation > 90f) {
                            rotationX = 180f
                        }
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (rotation > 90f) flashcard.back else flashcard.front,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconToggleButton(
                        checked = flashcard.favourite,
                        onCheckedChange = {
                            onFavouriteClick()
                        }
                    ) {
                        if (flashcard.favourite) {
                            Icon(SWDIcons.FavouriteFilled, null)
                        } else {
                            Icon(SWDIcons.FavouriteOutlined, null)
                        }
                    }
                }
            }
        }
    }
}


sealed class FlashcardBottomSheetEvent {
    object OnDismiss: FlashcardBottomSheetEvent()
    data class OnRemoveFlashcard(val flashcardId: Long): FlashcardBottomSheetEvent()
    data class OnEditFlashcard(val flashcardId: Long, val navController: NavController): FlashcardBottomSheetEvent()
}
@Composable
fun FlashcardBottomSheet(
    onEvent: (FlashcardBottomSheetEvent) -> Unit,
    noteId: Long,
    navController: NavController,
) {
    SWDBottomSheet(onDismiss = { onEvent(FlashcardBottomSheetEvent.OnDismiss) }, title = "Flashcard option") {
        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(FlashcardBottomSheetEvent.OnRemoveFlashcard(noteId)) },
            headlineContent = { Text(text = "Remove flashcard from deck") },
            leadingContent = {
                Icon(SWDIcons.DeleteOutlined, null)
            }
        )
        Divider(thickness = LocalDimensions.current.dividerThickness)
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onEvent(
                        FlashcardBottomSheetEvent.OnEditFlashcard(
                            noteId,
                            navController
                        )
                    )
                },
            headlineContent = { Text(text = "Edit flashcard") },
            leadingContent = {
                Icon(SWDIcons.Edit, null)
            }
        )
        Divider(thickness = LocalDimensions.current.dividerThickness)

    }
}

@Composable
fun OneSideFlashcardView(
    flashcards: List<Flashcard>,
    visibleBackFlashcardIds: Set<Long>,
    onClick: (Long) -> Unit,
    onFavouriteClick: (Long, Boolean) -> Unit,
    onMenuClick: (Long) -> Unit,
    onAddFlashcard: () -> Unit
) {

    if(flashcards.isEmpty()) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clickable(
                    onClick = { onAddFlashcard() }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LocalDimensions.current.defaultPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(text = "There is no flashcard in the deck", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                Text(text = "click here to add your first flashcard", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary)

            }
        }
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
        ) {
            items(flashcards.size) { index ->
                HorizontalFlashcardItem(
                    isFrontVisible = flashcards[index].flashcardId !in visibleBackFlashcardIds,
                    flashcard = flashcards[index],
                    onClick = { onClick(flashcards[index].flashcardId) },
                    onMenuClick = { onMenuClick(flashcards[index].flashcardId) },
                    onFavouriteClick = { onFavouriteClick(flashcards[index].flashcardId, !flashcards[index].favourite) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardNavigationMenu(
    numberOfFlashcardToRepeat: Int,
    onAdd: () -> Unit,
    onRepeat: () -> Unit,
    onQuiz: () -> Unit,
    onFlashcards: () -> Unit,
) {
    val iconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    )
    val textColor = MaterialTheme.colorScheme.onTertiaryContainer

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,

        ) {
        Column(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BadgedBox(badge = {
                if (numberOfFlashcardToRepeat > 0)
                    Badge(modifier = Modifier.graphicsLayer {
                        scaleX = 1.5f
                        scaleY = 1.5f
                        translationX = -12.dp.toPx()
                        translationY = 12.dp.toPx()
                    }) {
                        Text(text = numberOfFlashcardToRepeat.toString())
                    }
            }) {
                FilledIconButton(
                    onClick = { onRepeat() },
                    modifier = Modifier.size(LocalDimensions.current.bigIconButton),
                    colors = iconButtonColors
                )
                {
                    Icon(
                        SWDIcons.Repeat,
                        null,
                        modifier = Modifier.size(LocalDimensions.current.bigIcon)
                    )
                }
            }
            Text(text = "Repeat", color = textColor)
        }
        Column(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledIconButton(
                onClick = { onAdd() },
                modifier = Modifier.size(LocalDimensions.current.bigIconButton),
                colors = iconButtonColors
            ) {
                Icon(
                    SWDIcons.Add,
                    null,
                    modifier = Modifier.size(LocalDimensions.current.bigIcon)
                )
            }
            Text(text = "Add", color = textColor)
        }

        Column(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledIconButton(
                onClick = { onQuiz() },
                modifier = Modifier.size(LocalDimensions.current.bigIconButton),
                colors = iconButtonColors
            ) {
                Icon(
                    SWDIcons.QuizFilled,
                    null,
                    modifier = Modifier.size(LocalDimensions.current.bigIcon)
                )
            }
            Text(text = "Quiz", color = textColor)
        }

        Column(
            modifier = Modifier
                .weight(1f, false)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.halfDefaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledIconButton(
                onClick = { onFlashcards() },
                modifier = Modifier.size(LocalDimensions.current.bigIconButton),
                colors = iconButtonColors
            ) {
                Icon(
                    SWDIcons.FlashcardFilled,
                    null,
                    modifier = Modifier.size(LocalDimensions.current.bigIcon)
                )
            }
            Text(text = "Flashcards", color = textColor)
        }
    }
}


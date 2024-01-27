package com.example.studywithdaba.feature_flashcard.deck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.dabastudy.core.database.model.entities.Deck
import com.example.dabastudy.core.database.model.entities.relations.DeckWithDeckSummary
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme
import com.example.studywithdaba.feature_note.NotesEvent
import com.example.studywithdaba.feature_note.toTimeDateString


data class DecksState(
    val decks: List<DeckWithDeckSummary> = emptyList(),
    val gridSize: Int = 2,
    val selectedDecksIds: Set<Long> = emptySet()

)

sealed class DecksEvent {
    data class OnDeckClick(val deckId: Long): DecksEvent()
    data class OnDeckLongClick(val deckId: Long, val selectedChange: Boolean): DecksEvent()
    data class OnDeckFavouriteClick(val deckId: Long, val favouriteChange: Boolean): DecksEvent()
    object OnAddDeck: DecksEvent()
    data class OnDeckMoreClick(val deckId: Long): DecksEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksScreen(
    state: DecksState,
    onEvent: (DecksEvent) -> Unit,
    navController: NavController,
    innerPaddingValues: PaddingValues = PaddingValues()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPaddingValues.calculateBottomPadding())
        ) {
            CenterAlignedTopAppBar(title = { Text(text = "Flashcards Decks") })
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(state.gridSize), horizontalArrangement = Arrangement.spacedBy(
                    LocalDimensions.current.defaultPadding),
                verticalItemSpacing = LocalDimensions.current.defaultPadding
            ) {
                items(state.decks.size) { index ->
                    DeckItem(
                        deckSummary = state.decks[index],
                        selected = state.selectedDecksIds.isNotEmpty()
                                && state.decks[index].deck.deckId in state.selectedDecksIds,
                        onClick = {
                                  onEvent(DecksEvent.OnDeckClick(it))
                        },
                        onLongClick = { deckId, selectedChange ->
                              onEvent(DecksEvent.OnDeckLongClick(deckId, selectedChange))
                        },
                        onFavouriteClick =  { deckId, favouriteChange ->
                            onEvent(DecksEvent.OnDeckFavouriteClick(deckId, favouriteChange))
                        },
                        onMoreClick = {
                            onEvent(DecksEvent.OnDeckMoreClick(it))
                        }
                    )
                }
            }
        }
        FloatingActionButton(onClick = { onEvent(DecksEvent.OnAddDeck) }, modifier = Modifier
            .padding(
                LocalDimensions.current.defaultPadding
            )
            .align(Alignment.BottomEnd)) {
            Icon(imageVector = SWDIcons.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckItem(
    modifier: Modifier = Modifier,
    deckSummary: DeckWithDeckSummary,
    selected: Boolean,
    onClick: (Long) -> Unit,
    onLongClick: (Long, Boolean) -> Unit,
    onFavouriteClick: (Long, Boolean) -> Unit,
    onMoreClick: (Long) -> Unit,
) {
    Card(
        modifier = modifier.
        combinedClickable(
            onClick = { onClick(deckSummary.deck.deckId)},
            onLongClick = {onLongClick(deckSummary.deck.deckId, !selected)}
        ),
        border = if(selected)
            BorderStroke(LocalDimensions.current.highlightWidth, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                IconButton(onClick = { onFavouriteClick(deckSummary.deck.deckId, !deckSummary.deck.favourite) } ) {
                    if(deckSummary.deck.favourite)
                        Icon(SWDIcons.FavouriteFilled, null, tint = MaterialTheme.colorScheme.primary)
                    else
                        Icon(SWDIcons.FavouriteOutlined, null)
                }
                IconButton(onClick = { onMoreClick(deckSummary.deck.deckId) }, modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Max)
                    .width(intrinsicSize = IntrinsicSize.Max)
                ) {
                    Icon(imageVector = SWDIcons.DotMenu, contentDescription = null)
                }
            }

            Text(
                text = deckSummary.deck.title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(
                        horizontal = LocalDimensions.current.defaultPadding
                    )
            )
            Text(
                modifier = Modifier.padding(horizontal = LocalDimensions.current.defaultPadding),
                text = deckSummary.deck.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.halfDefaultPadding))
            Text(text = deckSummary.summaryToString(),  modifier = Modifier.padding(
                bottom = LocalDimensions.current.defaultPadding,
                start = LocalDimensions.current.defaultPadding,
                end = LocalDimensions.current.defaultPadding,
                ),
                style = MaterialTheme.typography.bodySmall
            )



        }
    }
}


@Preview
@Composable
fun DeckItemPreview() {
    StudyWithDabaTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(LocalDimensions.current.defaultPadding)
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(
                    LocalDimensions.current.defaultPadding),
                verticalItemSpacing = LocalDimensions.current.defaultPadding
            ) {
                items(5) {
                    DeckItem(
                        deckSummary = DeckWithDeckSummary(
                            Deck("title", "this is an example desription of an flashcard deck"),
                            2,
                            4,
                            5,
                            6,
                            2
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        selected = false,
                        onClick = {},
                        onLongClick = {_, _ -> },
                        onFavouriteClick = {_, _ ->},
                        onMoreClick = {}
                    )
                }
            }
        }
    }
}

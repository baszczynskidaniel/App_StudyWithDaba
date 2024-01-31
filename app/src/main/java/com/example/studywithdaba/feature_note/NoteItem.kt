package com.example.studywithdaba.feature_note

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import com.example.dabastudy.core.database.model.entities.Note
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteSummaryItem(
    modifier: Modifier = Modifier,
    note: Note,
    selected: Boolean,
    onClick: (Long) -> Unit,
    onLongClick: (Long, Boolean) -> Unit,
    onFavouriteClick: (Long, Boolean) -> Unit,
    onMoreClick: (Long) -> Unit,

    ) {
    Card(
        modifier = modifier.
            combinedClickable(
                onClick = { onClick(note.noteId)},
                onLongClick = {onLongClick(note.noteId, !selected)}

            )
        ,
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                IconButton(onClick = { onFavouriteClick(note.noteId, !note.favourite) }, modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Max)
                    .width(intrinsicSize = IntrinsicSize.Max)
                ) {
                    if(note.favourite)
                        Icon(SWDIcons.FavouriteFilled, null, tint = MaterialTheme.colorScheme.primary)
                    else
                        Icon(SWDIcons.FavouriteOutlined, null)
                }
                IconButton(onClick = { onMoreClick(note.noteId) }, modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Max)
                    .width(intrinsicSize = IntrinsicSize.Max)
                ) {
                    Icon(imageVector = SWDIcons.DotMenu, contentDescription = null)
                }
            }
            Text(
                text = note.title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    //.weight(1f)
                    .padding(
                        horizontal = LocalDimensions.current.defaultPadding
                    )
            )
            Spacer(modifier = Modifier.height(LocalDimensions.current.halfDefaultPadding))
            Text(
                text = note.lastEdited.toTimeDateString(),
                modifier = Modifier.padding(horizontal = LocalDimensions.current.defaultPadding),
                style = MaterialTheme.typography.bodySmall)

            Text(
                modifier = Modifier.padding(bottom = LocalDimensions.current.defaultPadding, start = LocalDimensions.current.defaultPadding, end = LocalDimensions.current.defaultPadding),
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun Long.toTimeDateString(): String {
    val dateTime = java.util.Date(this)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
    return format.format(dateTime)
}

@Preview
@Composable
fun NoteSummaryPreview() {
    StudyWithDabaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(LocalDimensions.current.defaultPadding)
        ) {
            LazyVerticalStaggeredGrid(
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                verticalItemSpacing = LocalDimensions.current.defaultPadding,
                columns = StaggeredGridCells.Fixed(2)
            ) {
                items(5) {
                    NoteSummaryItem(note =Note("title", content = "content"), selected = it % 3 == 0, onClick = {}, onLongClick = { noteId, selectedChange ->  }, onFavouriteClick = {noteId, favouriteChange ->}, onMoreClick = {})
                }
            }
        }
    }
}
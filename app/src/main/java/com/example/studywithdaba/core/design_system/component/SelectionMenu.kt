package com.example.studywithdaba.core.design_system.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.studywithdaba.core.design_system.theme.LocalDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Enum<T>>SelectionMenu(
    modifier: Modifier = Modifier,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    selected: T,
    onFilterItemClick: (T) -> Unit,
    values: Array<T>
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
    ) {
        values.forEach {
            FilterChip(

                colors = colors,
                selected = it == selected,
                onClick = { onFilterItemClick(it) },
                label = { Text(text = it.toString()) },
                border = null,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Enum<T>>SelectionMenuWithoutBorder(
    modifier: Modifier = Modifier,
    selected: T,
    onFilterItemClick: (T) -> Unit,
    values: Array<T>
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
    ) {
        values.forEach {
            ElevatedFilterChip(
                border = null,
                selected = it == selected,
                onClick = { onFilterItemClick(it) },
                label = { Text(text = it.toString()) },
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    selectedContainerColor = MaterialTheme.colorScheme.onSurface,
                    selectedLabelColor = MaterialTheme.colorScheme.surface
                )

            )
        }
    }
}



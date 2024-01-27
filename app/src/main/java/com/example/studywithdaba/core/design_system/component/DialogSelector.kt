package com.example.studywithdaba.core.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions

@Composable
fun <T: Enum<T>>DialogSelector(
    modifier: Modifier = Modifier,
    selectedItem: T,
    label: String,
    onItemClick: (T) -> Unit,
    values: Array<T>,
    onDismiss: (T) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss(selectedItem) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        ),
    ) {
        Card(
            modifier = modifier
                .width(LocalDimensions.current.dialogMaxWidth)
                .padding(LocalDimensions.current.doubleDefaultPadding),

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LocalDimensions.current.defaultPadding),
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(LocalDimensions.current.defaultPadding),
                    color = MaterialTheme.colorScheme.primary
                )
                LazyColumn(modifier = Modifier.weight(1f, false),
                    verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding)
                    ) {
                    items(values.size) {index ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable { onItemClick(values[index]) }
                            .background(
                                if (selectedItem == values[index])
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent
                            )
                            .padding(LocalDimensions.current.defaultPadding),
                            horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.defaultPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                fontWeight = if (selectedItem == values[index]) FontWeight.Bold else FontWeight.Normal,
                                text = values[index].toString(),
                                color = if (selectedItem == values[index]) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AnimatedVisibility(visible = selectedItem == values[index]) {
                                Icon(
                                    SWDIcons.Check,
                                    null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
                TextButton(
                    onClick = { onDismiss(selectedItem) }, modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Text(text = "Done")
                }
            }
        }
    }
}

@Composable
fun <T: Enum<T>>DialogSelectorWithState(
    modifier: Modifier = Modifier,
    initialValue: T,
    label: String,
    values: Array<T>,
    onDismiss: (T?) -> Unit
) {
    var selectedItem by remember {
        mutableStateOf(initialValue)
    }
    DialogSelector(
        selectedItem = selectedItem,
        label = label,
        onItemClick = { selectedItem = it},
        values = values,
        onDismiss = {
            val endValue = if(selectedItem == initialValue) null else selectedItem
            onDismiss(endValue)
        }
    )

}
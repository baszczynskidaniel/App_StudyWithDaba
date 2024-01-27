package com.example.studywithdaba.core.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme
import com.google.protobuf.value

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SWDBottomSheet(
    onDismiss: () -> Unit,
    title: String,
    content:  @Composable() (ColumnScope.() -> Unit)
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = LocalDimensions.current.defaultPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
            content()
        }
    }
}
interface BottomSheetMenuEvent {}

data class SWDBottomSheetMenuItem(
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val headline: String,
    val supportText: String? = null,
    val overLineText: String? = null,
    val event: BottomSheetMenuEvent
)

@Composable
fun SWDBottomSheetMenu(
    onDismiss: () -> Unit,
    title: String,
    onEvent: (BottomSheetMenuEvent) -> Unit,
    menuItems: List<SWDBottomSheetMenuItem>
) {
    SWDBottomSheet(
        onDismiss = { onDismiss() },
        title = title
    ) {
        Divider(thickness = 0.5.dp)
        LazyColumn(
            modifier = Modifier.weight(1f, false)
        ) {
        items(menuItems.size) {index ->
               ListItem(
                   modifier = Modifier
                       .fillMaxWidth()
                       .clickable { onEvent(menuItems[index].event) },
                   headlineContent = { Text(text = menuItems[index].headline) },
                   supportingContent = {
                       if(menuItems[index].supportText != null)
                           Text(menuItems[index].supportText!!)
                                       },
                   overlineContent = {
                       if(menuItems[index].overLineText != null)
                           Text(menuItems[index].overLineText!!)
                   },
                   leadingContent = {
                       if(menuItems[index].leadingIcon != null)
                           Icon(imageVector = menuItems[index].leadingIcon!!, contentDescription = null)
                   },
                   trailingContent = {
                       if(menuItems[index].trailingIcon != null)
                           Icon(imageVector = menuItems[index].trailingIcon!!, contentDescription = null)
                   },
               )
                Divider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun <T: Enum<T>> SWDSelectionBottomSheetWithState(
    initialValue: T,
    title: String,
    values: Array<T>,
    onDismiss: () -> Unit,
    onApply: (T) -> Unit,
    onReset: () -> Unit
) {
    var selectedItem by remember {
        mutableStateOf(initialValue)
    }
    SWDBottomSheet(
        onDismiss = { onDismiss() },
        title = title
    ) {
        Divider(thickness = 0.5.dp)
        LazyColumn(
            modifier = Modifier.weight(1f, false)
        ) {
            items(values.size) {index ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedItem = values[index] }
                ) {
                    Spacer(modifier = Modifier.height(LocalDimensions.current.halfDefaultPadding))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = values[index] == selectedItem,
                            onClick = { selectedItem = values[index] })
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = values[index].toString(),
                            fontWeight = if (values[index] == selectedItem) FontWeight.Bold else FontWeight.Normal,
                            color = if (values[index] == selectedItem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(LocalDimensions.current.halfDefaultPadding))
                }
                Divider(thickness = 0.5.dp)
            }
        }
        Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
        Button(onClick = { onApply(selectedItem) }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Apply")
        }
        Spacer(modifier = Modifier.height(LocalDimensions.current.defaultPadding))
        TextButton(onClick = { onReset() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Reset")
        }
    }
}






enum class PreviewValues {
    A,
    B,
    C;
}

@Preview
@Composable
fun SWDSelectionBottomSheetPreview() {
    StudyWithDabaTheme(
        darkTheme = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
        ) {
            SWDSelectionBottomSheetWithState(
                initialValue = PreviewValues.B,
                title = "Preview",
                onDismiss = {  },
                onApply = {},
                onReset = {},
                values = PreviewValues.values()
            )
        }
    }
}

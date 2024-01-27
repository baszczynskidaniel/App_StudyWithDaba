package com.example.studywithdaba.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.studywithdaba.core.datastore.model.AppTheme
import com.example.studywithdaba.core.design_system.component.DialogSelector
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.LocalDimensions
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme

@Composable
fun SettingScreen(
    onEvent: (SettingsEvent) -> Unit,
    state: SettingsState,
    navController: NavController?
) {
    SettingsScreen(
        modifier = Modifier,
        onBack = {
            onEvent(SettingsEvent.OnBack(navController!!))
        },
        appTheme = state.userData.theme,
        useDynamicTheme = state.userData.dynamicColor,
        onDynamicThemeChange = {
            onEvent(SettingsEvent.OnUseDynamicColorChange(it))
        },
        onAppThemeChange = {
            onEvent(SettingsEvent.OnAppThemeChange(it))
        },
        showThemeDialog = state.showThemeDialog,
        onAppearanceClick = {
            onEvent(SettingsEvent.OnAppearanceClick)
        },
        onAppThemeDialogDismiss = {
            onEvent(SettingsEvent.OnAppThemeDialogDismiss)
        }
    )
}

@Composable 
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    appTheme: AppTheme,
    useDynamicTheme: Boolean,
    onDynamicThemeChange: (Boolean) -> Unit,
    onAppThemeChange: (AppTheme) -> Unit,
    showThemeDialog: Boolean,
    onAppearanceClick: () -> Unit,
    onAppThemeDialogDismiss: () -> Unit,

) {
    Column(
        modifier = Modifier.fillMaxSize(),

    ) {
        SettingsTopAppBar(
            onBack = {onBack()}
        )

        LazyColumn()  {
            item() {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                onAppearanceClick()
                            }
                        ),
                    leadingContent = {
                        Icon(SWDIcons.Theme, null)
                    },
                    headlineContent = {
                        Text(text = "Appearance")
                    },
                    trailingContent = {
                         Text(text = appTheme.toString(), color = MaterialTheme.colorScheme.primary)
                    },
                )
               ListItem(
                   leadingContent = {
                        Icon(SWDIcons.DynamicColor, null)
                   },
                   headlineContent = {
                       Text(text = "Use dynamic color")
                   },
                   supportingContent = {
                       Text("Make the interface more personalized ")
                   },
                   trailingContent = {
                       Switch(
                           checked = useDynamicTheme,
                           onCheckedChange = {onDynamicThemeChange(useDynamicTheme)})
                   }
               )
            }
        }
    }
    if(showThemeDialog)
        DialogSelector(selectedItem = appTheme, label = "Appearance", onItemClick = { onAppThemeChange(it)}, values = AppTheme.values(), onDismiss = {onAppThemeDialogDismiss()})
}

@Preview
@Composable
fun SettingsScreenPreview() {
    var appTheme by remember {
        mutableStateOf(AppTheme.Default)
    }
    var showChangeAppThemeDialog by remember {
        mutableStateOf(false)
    }
    var useDynamicTheme by remember {
        mutableStateOf(false)
    }
    StudyWithDabaTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SettingsScreen(
                onBack = { /*TODO*/ },
                appTheme = appTheme,
                onDynamicThemeChange = { useDynamicTheme = !useDynamicTheme },
                onAppThemeChange = { appTheme = it},
                showThemeDialog = showChangeAppThemeDialog,
                onAppearanceClick = { showChangeAppThemeDialog = true },
                onAppThemeDialogDismiss = { showChangeAppThemeDialog = false },
                useDynamicTheme = useDynamicTheme,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(text = "Settings") },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(SWDIcons.Back, null)
            }
        },
    )
}

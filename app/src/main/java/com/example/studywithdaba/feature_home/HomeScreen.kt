package com.example.studywithdaba.feature_home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.design_system.icon.SWDIcons

@Composable
fun HomeScreen(
    navController: NavController,
    innerPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
          //  .padding(innerPadding)
    ) {
        HomeScreenTopBar(onSettings = { navController.navigate(Screen.Settings.route) }, onSearch = {})

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenTopBar(
    onSettings: () -> Unit,
    onSearch: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = "Study with daba")},
        actions = {
            IconButton(onClick = { onSearch() }) {
                Icon(SWDIcons.Search, null)
            }
            IconButton(onClick = { onSettings() }) {
                Icon(SWDIcons.SettingsFilled, null)
            }
        }
    )
}
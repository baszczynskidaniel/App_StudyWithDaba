package com.example.studywithdaba.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun rememberSWDAppState(
    navController: NavHostController
): SWDAppState {
    return remember(
        navController
    ) {
        SWDAppState(
            navController
        )
    }
}

@Stable
class SWDAppState(
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination
}
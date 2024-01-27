package com.example.studywithdaba.core.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.design_system.icon.SWDIcons
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme

@Composable
fun SWDNavigationBar(
    currentRoute: String?,
    onClick: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,

) {
    val navigationBarDestination = NavigationBarDestination.values()
    val addButtonIndex = navigationBarDestination.size / 2



    NavigationBar(
        modifier = modifier,
        windowInsets = WindowInsets.navigationBars
    ) {
        navigationBarDestination.forEachIndexed { index, navigationBarDestination ->
            if(index == addButtonIndex) {
                OutlinedIconButton(
                    modifier = Modifier
                        .height(intrinsicSize = IntrinsicSize.Max)
                        .width(intrinsicSize = IntrinsicSize.Max)
                        .align(Alignment.CenterVertically),
                    onClick = { onAdd() },
                ) {
                    Icon(imageVector = SWDIcons.Add, contentDescription = "Add")
                }
            }
            NavigationBarItem(
                selected = currentRoute == navigationBarDestination.route,
                onClick = { onClick(navigationBarDestination.route) },
                icon = {
                    if (currentRoute == navigationBarDestination.route)
                        Icon(navigationBarDestination.selectedIcon, navigationBarDestination.iconText)
                    else
                        Icon(navigationBarDestination.unselectedIcon, navigationBarDestination.iconText)
                },
                label = { Text(text = navigationBarDestination.iconText) }
            )
        }

    }
}

@Preview
@Composable
fun SWDNavigationBarPreview() {
    var currentRoute by remember {
        mutableStateOf(NavigationBarDestination.HOME.route)
    }
    StudyWithDabaTheme {
        Surface(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {

        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                SWDNavigationBar(
                    currentRoute = currentRoute,
                    onClick = { currentRoute = it },
                    onAdd = {}
                    )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {

            }
        }
    }
}


enum class NavigationBarDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconText: String,
    val title: String,
    val route: String
) {

    HOME(
        selectedIcon = SWDIcons.HomeFilled,
        unselectedIcon = SWDIcons.HomeOutlined,
        iconText = "Home",
        title = "home",
        route = Screen.Home.route
    ),
    NOTES(
        selectedIcon = SWDIcons.NoteFilled,
        unselectedIcon = SWDIcons.NoteOutlined,
        iconText = "Notes",
        title = "Notes",
        route = Screen.Notes.route
    ),
    FLASHCARDS(
        selectedIcon = SWDIcons.FlashcardFilled,
        unselectedIcon = SWDIcons.FlashcardOutlined,
        iconText = "Flashcards",
        title = "Flashcards",
        route = "decks"
    ),
    STUDY(
        selectedIcon = SWDIcons.StudyFilled,
        unselectedIcon = SWDIcons.StudyOutlined,
        iconText = "Study",
        title = "Study",
        route = "study"
    ),

}

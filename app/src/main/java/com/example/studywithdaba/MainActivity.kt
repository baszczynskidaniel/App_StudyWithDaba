package com.example.studywithdaba

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studywithdaba.Navigation.NavigationGraph
import com.example.studywithdaba.Navigation.Screen
import com.example.studywithdaba.core.data.util.DefaultSpanStyle
import com.example.studywithdaba.core.datastore.model.AppTheme
import com.example.studywithdaba.core.design_system.component.AddFeaturesBottomSheet
import com.example.studywithdaba.core.design_system.component.AddFeaturesBottomSheetEvent
import com.example.studywithdaba.core.design_system.component.SWDNavigationBar
import com.example.studywithdaba.core.design_system.theme.StudyWithDabaTheme
import com.example.studywithdaba.feature_deck.add_deck.AddDeckDialog
import com.example.studywithdaba.feature_deck.add_deck.AddEditDeckViewModel
import com.example.studywithdaba.feature_deck.add_deck.addEditDeckViewModel
import com.example.studywithdaba.feature_flashcard.add_flashcard.AddEditFlashcardDialog
import com.example.studywithdaba.feature_flashcard.add_flashcard.AddEditFlashcardViewModel
import com.example.studywithdaba.feature_flashcard.add_flashcard.FlashcardAndDeckId
import com.example.studywithdaba.feature_flashcard.add_flashcard.addEditFlashcardViewModel
import com.example.studywithdaba.feature_flashcard.flashcards_review_settings.FlashcardsReviewSettingsViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun addEditDeckViewModel(): AddEditDeckViewModel.Factory
        fun addEditFlashcardViewModel(): AddEditFlashcardViewModel.Factory

        fun flashcardsReviewSettingsViewModelFactory(): FlashcardsReviewSettingsViewModel.Factory
    }





    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)





           setContent {

               val state = viewModel.state.collectAsState()
               val navController = rememberNavController()
               val navBackStackEntry by navController.currentBackStackEntryAsState()
               val currentRoute = navBackStackEntry?.destination?.route
               if(!state.value.isLoading) {
                   StudyWithDabaTheme(
                       darkTheme = shouldUseDarkTheme(state.value.userData.theme),
                       dynamicColor = state.value.userData.dynamicColor
                   ) {
                       DefaultSpanStyle.spanStyle = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(color = MaterialTheme.colorScheme.onBackground)
                       // A surface container using the 'background' color from the theme
                       Surface(
                           modifier = Modifier.fillMaxSize(),
                           color = MaterialTheme.colorScheme.background
                       ) {
                           MainScaffold(
                               state = state.value,
                               currentRoute = currentRoute,
                               onEvent = viewModel::onEvent,
                               navController = navController
                           )
                       }
                   }
               }
            }
        }

    }


@Composable
private fun shouldUseDarkTheme(appTheme: AppTheme): Boolean
{
    return when(appTheme) {
        AppTheme.Light -> false
        AppTheme.Dark -> true
        AppTheme.Default -> isSystemInDarkTheme()
    }
}

@Composable
fun showNavigationBar(route: String): Boolean {
    return when {
        route.isNullOrBlank() -> true
        else -> true
    }
}


sealed class MainActivityEvent {
    data class OnCurrentRouteChange(val route: String, val navController: NavHostController): MainActivityEvent()
    object OnAdd: MainActivityEvent()
    object OnDismissAddFlashcardDialog: MainActivityEvent()
    object OnDismissAddDeckDialog: MainActivityEvent()
    data class OnBottomSheetEvent(val event: AddFeaturesBottomSheetEvent, val navController: NavController): MainActivityEvent()
}

@Composable
fun MainScaffold(
    state: MainActivityState,
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onEvent: (MainActivityEvent) -> Unit,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        modifier = modifier
            .statusBarsPadding(),
           // .navigationBarsPadding(),
        bottomBar = {
            AnimatedVisibility(visible =
                    currentRoute != null && (
                    currentRoute.startsWith(Screen.Home.route) ||
                    currentRoute.startsWith(Screen.Notes.route) ||
                    currentRoute.startsWith(Screen.Decks.route))) {
                SWDNavigationBar(
                    currentRoute = currentRoute,
                    onClick = {onEvent(MainActivityEvent.OnCurrentRouteChange(it, navController))},
                    onAdd = {
                        onEvent(MainActivityEvent.OnAdd)
                    }
                )
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            innerPadding = innerPadding
        )
        if(state.showBottomSheet) {
            AddFeaturesBottomSheet(onEvent = {onEvent(MainActivityEvent.OnBottomSheetEvent(it, navController))})
        }
    }
}

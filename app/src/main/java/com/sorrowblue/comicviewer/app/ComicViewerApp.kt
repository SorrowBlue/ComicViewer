package com.sorrowblue.comicviewer.app

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper
import com.sorrowblue.comicviewer.app.component.BuildTypeStatusBar
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.MainDependencies
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationArgs
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreen
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.Mode
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreen
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.rememberSlideDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@Destination<RootGraph>(
    start = true,
    visibility = CodeGenVisibility.INTERNAL,
    wrappers = [RootScreenWrapper::class]
)
@Composable
internal fun ComicViewerApp(
    state: ComicViewerAppState = rememberComicViewerAppState(),
) {
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        DestinationsNavHost(
            navGraph = NavGraphs.main,
            navController = state.navController,
            dependenciesContainerBuilder = {
                MainDependencies(onRestoreComplete = state::onNavigationHistoryRestore)

                state.addOnList.forEach { addOn ->
                    addOn.findNavGraph()?.let { navGraph ->
                        with(navGraph) {
                            Dependency()
                        }
                    }
                }
            },
        )
    }
    BuildTypeStatusBar(BuildConfig.BUILD_TYPE)

    rememberSlideDistance().let { slideDistance ->
        LaunchedEffect(slideDistance) {
            DestinationTransitions.slideDistance = slideDistance
        }
    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::refreshAddOnList)
}

@HiltViewModel
internal class RootScreenWrapperViewModel @Inject constructor(
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
) : ViewModel() {

    val requireTutorial = loadSettingsUseCase.settings.map { !it.doneTutorial }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun tutorialComplete() {
        viewModelScope.launch {
            loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
        }
    }

    val requireAuth = manageSecuritySettingsUseCase.settings.map { it.password != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}

internal object RootScreenWrapper : DestinationWrapper {

    @Composable
    override fun <T> DestinationScope<T>.Wrap(
        @SuppressLint("ComposableLambdaParameterNaming") screenContent: @Composable () -> Unit,
    ) {
        val viewModel = hiltViewModel<RootScreenWrapperViewModel>()
        val mainViewModel = hiltViewModel<MainViewModel>(LocalContext.current as ComponentActivity)
        val isTutorial by viewModel.requireTutorial.collectAsState()
        val requireAuth by viewModel.requireAuth.collectAsState()
        var authed by rememberSaveable { mutableStateOf(false) }
        val isInitialized by mainViewModel.isInitialized.collectAsState()

        logcat(
            "DEBUG"
        ) { "isTutorial=$isTutorial, requireAuth=$requireAuth, authed=$authed, isInitialized=$isInitialized" }
        if (isTutorial) {
            TutorialScreen(navigator = {
                viewModel.tutorialComplete()
            })
            SideEffect {
                mainViewModel.shouldKeepSplash.value = false
            }
        } else {
            if (!requireAuth || isInitialized || authed) {
                screenContent()
            }
            AnimatedVisibility(
                visible = (requireAuth && !authed) || (authed && !isInitialized),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                val activity = LocalContext.current as Activity
                AuthenticationScreen(
                    args = AuthenticationArgs(Mode.Authentication),
                    navigator = object : AuthenticationScreenNavigator {
                        override fun navigateUp() {
                            activity.finish()
                        }

                        override fun onCompleted() {
                            authed = true
                        }
                    }
                )
                SideEffect {
                    mainViewModel.shouldKeepSplash.value = false
                }
            }
        }
    }
}

fun AddOn.findNavGraph(): AddOnNavGraph? {
    return when (this) {
        AddOn.Document -> null
        AddOn.GoogleDrive -> GoogleDriveNavGraph()
        AddOn.OneDrive -> OneDriveNavGraph()
        AddOn.Dropbox -> DropBoxNavGraph()
        AddOn.Box -> BoxNavGraph()
    }
}

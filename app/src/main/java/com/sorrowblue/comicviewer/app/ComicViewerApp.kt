package com.sorrowblue.comicviewer.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import com.sorrowblue.comicviewer.app.navigation.MainDependencies
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.library.serviceloader.AddOnNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.rememberSlideDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Comic viewer app
 *
 * @param state
 */
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
            engine = rememberCustomNavHostEngine(),
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
    rememberSlideDistance().let { slideDistance ->
        LaunchedEffect(slideDistance) {
            DestinationTransitions.slideDistance = slideDistance
        }
    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::refreshAddOnList)
}

/**
 * Find nav graph
 *
 * @return
 */
fun AddOn.findNavGraph(): AddOnNavGraph? {
    return when (this) {
        AddOn.Document -> null
        AddOn.GoogleDrive -> GoogleDriveNavGraph()
        AddOn.OneDrive -> OneDriveNavGraph()
        AddOn.Dropbox -> DropBoxNavGraph()
        AddOn.Box -> BoxNavGraph()
    }
}

/**
 * Error alert dialog
 *
 */
@Preview
@Composable
private fun ErrorAlertDialog() {
    ComicTheme {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { }) {
                    Text(text = "送信")
                }
            },
            icon = {
                Icon(imageVector = ComicIcons.ErrorOutline, contentDescription = null)
            },
            title = {
                Text(text = "アプリケーションエラーが発生しました。")
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = "エラー内容を送信しますか？")
                    Text(
                        text = """
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                    error message here error message here error message here error message here
                        """.trimIndent()
                    )
                }
            }
        )
    }
}

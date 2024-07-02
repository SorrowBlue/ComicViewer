package com.sorrowblue.comicviewer.feature.library

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.feature.library.component.LibraryTopAppBar
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryGraphTransitions
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.feature.library.section.FeatureListSheet
import com.sorrowblue.comicviewer.feature.library.section.LibraryCloudStorageDialog
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

internal sealed interface LibraryScreenUiEvent {
    data class Message(
        val text: String,
        val actionLabel: String? = null,
        val withDismissAction: Boolean = false,
        val duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        val action: ((SnackbarResult) -> Unit)? = null,
    ) : LibraryScreenUiEvent {
        suspend fun showSnackbar(snackbarHostState: SnackbarHostState) {
            val result = snackbarHostState.showSnackbar(
                message = text,
                actionLabel = actionLabel,
                withDismissAction = withDismissAction,
                duration = duration
            )
            action?.invoke(result)
        }
    }

    data object Restart : LibraryScreenUiEvent
}

interface LibraryScreenNavigator {
    fun onFeatureClick(feature: Feature)
}

@Destination<LibraryGraph>(
    start = true,
    style = LibraryGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun LibraryScreen(navigator: LibraryScreenNavigator) {
    LibraryScreen(onFeatureClick = navigator::onFeatureClick)
}

@Composable
private fun LibraryScreen(
    onFeatureClick: (Feature) -> Unit,
    state: LibraryScreenState = rememberLibraryScreenState(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState = state.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    if (state.uiEvent.isNotEmpty()) {
        state.uiEvent.forEach {
            when (it) {
                is LibraryScreenUiEvent.Message -> scope.launch {
                    it.showSnackbar(snackbarHostState)
                }

                LibraryScreenUiEvent.Restart -> ActivityCompat.recreate(context as Activity)
            }
        }
    }
    LibraryScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onFeatureClick = { state.onFeatureClick(it, onFeatureClick) },
        onInstallClick = state::onInstallClick,
        onCancelClick = state::onCancelClick,
    )
    LifecycleEventEffect(event = Lifecycle.Event.ON_START, onEvent = state::onStart)
    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = state::onResume)
    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP, onEvent = state::onStop)
}

internal data class LibraryScreenUiState(
    val addOnList: PersistentList<Feature.AddOn> = persistentListOf(),
    val requestInstallAddOn: Feature.AddOn? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    uiState: LibraryScreenUiState,
    snackbarHostState: SnackbarHostState,
    onFeatureClick: (Feature) -> Unit,
    onInstallClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            LibraryTopAppBar(scrollBehavior = scrollBehavior)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        FeatureListSheet(
            basicList = remember { Feature.Basic.entries.toPersistentList() },
            addOnList = uiState.addOnList,
            contentPadding = contentPadding,
            onClick = onFeatureClick
        )
    }
    if (uiState.requestInstallAddOn != null) {
        LibraryCloudStorageDialog(
            addOn = uiState.requestInstallAddOn,
            onInstallClick = onInstallClick,
            onCancelClick = onCancelClick
        )
    }
}

@Preview
@Composable
private fun PreviewLibraryScreen() {
    ComicTheme {
        val addOns = listOf(
            Feature.AddOn.GoogleDrive(AddOnItemState.Still),
            Feature.AddOn.OneDrive(AddOnItemState.Installing),
            Feature.AddOn.Dropbox(AddOnItemState.Installed),
            Feature.AddOn.Box(AddOnItemState.Failed)
        )
        val uiState =
            LibraryScreenUiState(addOnList = addOns.toPersistentList())
        LibraryScreen(
            uiState = uiState,
            snackbarHostState = remember { SnackbarHostState() },
            onFeatureClick = { },
            onInstallClick = { },
            onCancelClick = { }
        )
    }
}

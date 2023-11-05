package com.sorrowblue.comicviewer.feature.library

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.feature.library.component.LibraryTopAppBar
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.feature.library.section.FeatureListSheet
import com.sorrowblue.comicviewer.feature.library.section.LibraryCloudStorageDialog
import com.sorrowblue.comicviewer.feature.library.section.RequestInstallDialogUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.flow.CollectAsEffect
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal class StringResource(private val resId: Int, private vararg val formatArgs: Any) {
    fun getString(context: Context): String {
        return context.getString(resId, *formatArgs)
    }
}

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

    data class Message2(
        val text: StringResource,
        val actionLabel: StringResource? = null,
        val withDismissAction: Boolean = false,
        val duration: SnackbarDuration = if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite,
        val action: ((SnackbarResult) -> Unit)? = null,
    ) : LibraryScreenUiEvent {

        suspend fun showSnackbar(snackbarHostState: SnackbarHostState, context: Context) {
            val result = snackbarHostState.showSnackbar(
                message = text.getString(context),
                actionLabel = actionLabel?.getString(context),
                withDismissAction = withDismissAction,
                duration = duration
            )
            action?.invoke(result)
        }
    }

    data class ClickFeature(val fetcher: Feature) : LibraryScreenUiEvent

    data object Restart : LibraryScreenUiEvent
}

@Composable
internal fun LibraryRoute(
    contentPadding: PaddingValues,
    onFeatureClick: (Feature) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    viewModel.uiEvent.CollectAsEffect {
        when (it) {
            is LibraryScreenUiEvent.Message -> it.showSnackbar(snackbarHostState)
            is LibraryScreenUiEvent.Message2 -> it.showSnackbar(snackbarHostState, context)
            is LibraryScreenUiEvent.ClickFeature -> onFeatureClick(it.fetcher)
            LibraryScreenUiEvent.Restart -> ActivityCompat.recreate(context as Activity)
        }
    }
    LibraryScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        contentPadding = contentPadding,
        onFeatureClick = viewModel::onFeatureClick,
        onInstallClick = viewModel::startInstall,
        onCancelClick = viewModel::closeDialog,
    )
    LifecycleEffect(lifecycleObserver = viewModel)
}

internal data class LibraryScreenUiState(
    val addOnList: PersistentList<Feature.AddOn>,
    val requestInstallDialogUiState: RequestInstallDialogUiState = RequestInstallDialogUiState.Hide,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    uiState: LibraryScreenUiState,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onFeatureClick: (Feature) -> Unit,
    onInstallClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localLayoutDirection = LocalLayoutDirection.current
    Scaffold(
        topBar = {
            LibraryTopAppBar(scrollBehavior = scrollBehavior)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        contentWindowInsets = WindowInsets(
            left = contentPadding.calculateLeftPadding(localLayoutDirection),
            top = contentPadding.calculateTopPadding(),
            right = contentPadding.calculateRightPadding(localLayoutDirection),
            bottom = contentPadding.calculateBottomPadding()
        ),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        FeatureListSheet(
            basicList = remember { Feature.Basic.entries.toPersistentList() },
            addOnList = uiState.addOnList,
            contentPadding = innerPadding,
            onClick = onFeatureClick
        )
    }
    LibraryCloudStorageDialog(
        uiState = uiState.requestInstallDialogUiState,
        onInstallClick = onInstallClick,
        onCancelClick = onCancelClick
    )
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
            contentPadding = PaddingValues(),
            onFeatureClick = { },
            onInstallClick = { },
            onCancelClick = { }
        )
    }
}

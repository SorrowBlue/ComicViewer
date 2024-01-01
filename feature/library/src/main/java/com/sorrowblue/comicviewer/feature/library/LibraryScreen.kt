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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.google.android.play.core.ktx.errorCode
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.status
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.feature.library.component.LibraryTopAppBar
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.feature.library.section.FeatureListSheet
import com.sorrowblue.comicviewer.feature.library.section.LibraryCloudStorageDialog
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
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

@Destination
@Composable
internal fun LibraryScreen(
    contentPadding: PaddingValues,
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
        contentPadding = contentPadding,
        onFeatureClick = { state.onFeatureClick(it, onFeatureClick) },
        onInstallClick = state::onInstallClick,
        onCancelClick = state::onCancelClick,
    )
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_START, action = state::onStart)
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_RESUME, action = state::onResume)
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_STOP, action = state::onStop)
}

internal data class LibraryScreenUiState(
    val addOnList: PersistentList<Feature.AddOn> = persistentListOf(),
    val requestInstallAddOn: Feature.AddOn? = null,
)

@Stable
internal class LibraryScreenState(
    private val context: Context,
    private val scope: CoroutineScope,
    private val splitInstallManager: SplitInstallManager,
) : SplitInstallStateUpdatedListener {

    var uiState by mutableStateOf(LibraryScreenUiState())
        private set

    var uiEvent by mutableStateOf(emptyList<LibraryScreenUiEvent>())
        private set

    init {
        val installedModules = splitInstallManager.installedModules
        val addOns = listOf(
            Feature.AddOn.GoogleDrive(AddOnItemState.Still),
            Feature.AddOn.OneDrive(AddOnItemState.Still),
            Feature.AddOn.Dropbox(AddOnItemState.Still),
            Feature.AddOn.Box(AddOnItemState.Still)
        ).map {
            if (installedModules.contains(it.addOn.moduleName)) it.copy2(AddOnItemState.Installed) else it
        }
        uiState = LibraryScreenUiState(addOnList = addOns.toPersistentList())
    }

    fun onStart() {
        splitInstallManager.registerListener(this)
    }

    fun onResume() {
        val installedModules = splitInstallManager.installedModules
        val list = uiState.addOnList.map {
            it.copy2(
                if (installedModules.contains(it.addOn.moduleName)) AddOnItemState.Installed else AddOnItemState.Still
            )
        }
        uiState = uiState.copy(addOnList = list.toPersistentList())
    }

    fun onStop() {
        splitInstallManager.unregisterListener(this)
    }

    override fun onStateUpdate(state: SplitInstallSessionState) {
        val featureList = uiState.addOnList.map { addOn ->
            if (state.moduleNames.contains(addOn.addOn.moduleName)) {
                val addOnItemState = when (state.status) {
                    SplitInstallSessionStatus.CANCELED -> AddOnItemState.Still

                    SplitInstallSessionStatus.FAILED -> {
                        uiEvent += LibraryScreenUiEvent.Message(
                            text = context.getString(
                                R.string.library_message_addon_install_failed,
                                state.errorCode
                            )
                        )
                        AddOnItemState.Failed
                    }

                    SplitInstallSessionStatus.CANCELING,
                    SplitInstallSessionStatus.DOWNLOADED,
                    SplitInstallSessionStatus.DOWNLOADING,
                    SplitInstallSessionStatus.INSTALLING,
                    SplitInstallSessionStatus.PENDING,
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION,
                    SplitInstallSessionStatus.UNKNOWN,
                    -> AddOnItemState.Installing

                    SplitInstallSessionStatus.INSTALLED -> {
                        uiEvent += LibraryScreenUiEvent.Message(
                            text = context.getString(
                                R.string.library_message_addon_installed,
                                addOn.addOn.moduleName
                            ),
                            actionLabel = context.getString(R.string.library_label_restart),
                            duration = SnackbarDuration.Long
                        ) {
                            if (it == SnackbarResult.ActionPerformed) {
                                uiEvent += LibraryScreenUiEvent.Restart
                            }
                        }
                        AddOnItemState.Restart
                    }

                    else -> AddOnItemState.Installing
                }
                addOn.copy2(state = addOnItemState)
            } else {
                addOn
            }
        }.toPersistentList()
        uiState = uiState.copy(addOnList = featureList)
    }

    fun onFeatureClick(feature: Feature, onClick: (Feature) -> Unit) {
        when (feature) {
            is Feature.AddOn -> {
                when (feature.state) {
                    AddOnItemState.Still -> requestInstall(feature)
                    AddOnItemState.Installing -> Unit
                    AddOnItemState.Installed -> onClick(feature)

                    AddOnItemState.Restart ->
                        uiEvent += LibraryScreenUiEvent.Message(
                            text = context.getString(R.string.library_message_restart_app),
                            actionLabel = context.getString(R.string.library_label_restart)
                        ) {
                            if (it == SnackbarResult.ActionPerformed) {
                                uiEvent += LibraryScreenUiEvent.Restart
                            }
                        }

                    AddOnItemState.Failed -> requestInstall(feature)
                }
            }

            is Feature.Basic -> onClick(feature)
        }
    }

    private fun requestInstall(addOn: Feature.AddOn) {
        uiState = uiState.copy(requestInstallAddOn = addOn)
    }

    fun onInstallClick() {
        if (uiState.requestInstallAddOn != null) {
            val feature = uiState.requestInstallAddOn!!
            uiState = uiState.copy(requestInstallAddOn = null)
            scope.launch {
                if (splitInstallManager.isInstallAllowed()) {
                    val state = kotlin.runCatching {
                        splitInstallManager.requestInstall(listOf(feature.addOn.moduleName))
                    }.onFailure { throwable ->
                        if (throwable is SplitInstallException) {
                            uiEvent += LibraryScreenUiEvent.Message(
                                text = context.getString(
                                    R.string.library_message_addon_install_failed,
                                    throwable.errorCode
                                )
                            )
                            uiState = uiState.copy(
                                addOnList = uiState.addOnList.map {
                                    if (it == feature) {
                                        it.copy2(state = AddOnItemState.Failed)
                                    } else {
                                        it
                                    }
                                }.toPersistentList()
                            )
                        }
                    }.getOrNull()
                    if (state == 0) {
                        // 既にインストールされている場合
                        uiState = uiState.copy(
                            addOnList = uiState.addOnList.map {
                                if (it == feature) {
                                    it.copy2(state = AddOnItemState.Installed)
                                } else {
                                    it
                                }
                            }.toPersistentList()
                        )
                        uiEvent += LibraryScreenUiEvent.Message(text = context.getString(R.string.library_message_addon_already_installed))
                    } else if (state != null) {
                        uiEvent += LibraryScreenUiEvent.Message(
                            text = context.getString(
                                R.string.library_message_addon_installing,
                                feature.addOn.moduleName
                            )
                        )
                    }
                } else {
                    uiEvent += LibraryScreenUiEvent.Message(text = context.getString(R.string.library_message_other_addon_installing))
                }
            }
        }
    }

    fun onCancelClick() {
        uiState = uiState.copy(requestInstallAddOn = null)
    }
}

@Composable
internal fun rememberLibraryScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: LibraryViewModel = hiltViewModel(),
) = remember {
    LibraryScreenState(
        context = context,
        scope = scope,
        splitInstallManager = viewModel.splitInstallManager
    )
}

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
            contentPadding = PaddingValues(),
            onFeatureClick = { },
            onInstallClick = { },
            onCancelClick = { }
        )
    }
}

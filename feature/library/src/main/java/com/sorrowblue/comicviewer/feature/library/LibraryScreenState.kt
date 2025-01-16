package com.sorrowblue.comicviewer.feature.library

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.compose.viewmodel.koinViewModel
import com.google.android.play.core.ktx.errorCode
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.status
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.feature.library.component.AddOnItemState
import com.sorrowblue.comicviewer.feature.library.section.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
internal interface LibraryScreenState {
    val uiState: LibraryScreenUiState
    val uiEvent: List<LibraryScreenUiEvent>
    fun onStart()
    fun onResume()
    fun onStop()
    fun onFeatureClick(feature: Feature, onClick: (Feature) -> Unit)
    fun onInstallClick()
    fun onCancelClick()
}

@Composable
internal fun rememberLibraryScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: LibraryViewModel = koinViewModel(),
): LibraryScreenState = remember {
    LibraryScreenStateImpl(
        context = context,
        scope = scope,
        splitInstallManager = viewModel.splitInstallManager
    )
}

private class LibraryScreenStateImpl(
    private val context: Context,
    private val scope: CoroutineScope,
    private val splitInstallManager: SplitInstallManager,
) : LibraryScreenState, SplitInstallStateUpdatedListener {

    override var uiState by mutableStateOf(LibraryScreenUiState())
        private set

    override var uiEvent by mutableStateOf(emptyList<LibraryScreenUiEvent>())
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
        uiState = LibraryScreenUiState(addOnList = addOns)
    }

    override fun onStart() {
        splitInstallManager.registerListener(this)
    }

    override fun onResume() {
        val installedModules = splitInstallManager.installedModules
        val list = uiState.addOnList.map {
            it.copy2(
                if (installedModules.contains(it.addOn.moduleName)) AddOnItemState.Installed else AddOnItemState.Still
            )
        }
        uiState = uiState.copy(addOnList = list)
    }

    override fun onStop() {
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
                            duration = androidx.compose.material3.SnackbarDuration.Long
                        ) {
                            if (it == androidx.compose.material3.SnackbarResult.ActionPerformed) {
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
        }
        uiState = uiState.copy(addOnList = featureList)
    }

    override fun onFeatureClick(feature: Feature, onClick: (Feature) -> Unit) {
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
                            if (it == androidx.compose.material3.SnackbarResult.ActionPerformed) {
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

    override fun onInstallClick() {
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
                                }
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
                            }
                        )
                        uiEvent += LibraryScreenUiEvent.Message(
                            text = context.getString(R.string.library_message_addon_already_installed)
                        )
                    } else if (state != null) {
                        uiEvent += LibraryScreenUiEvent.Message(
                            text = context.getString(
                                R.string.library_message_addon_installing,
                                feature.addOn.moduleName
                            )
                        )
                    }
                } else {
                    uiEvent += LibraryScreenUiEvent.Message(
                        text = context.getString(R.string.library_message_other_addon_installing)
                    )
                }
            }
        }
    }

    override fun onCancelClick() {
        uiState = uiState.copy(requestInstallAddOn = null)
    }
}

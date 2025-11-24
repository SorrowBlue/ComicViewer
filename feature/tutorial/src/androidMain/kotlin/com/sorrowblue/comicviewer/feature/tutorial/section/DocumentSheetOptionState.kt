package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.sorrowblue.comicviewer.domain.model.onError
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.GetPdfPluginStateUseCase
import com.sorrowblue.comicviewer.domain.usecase.PACKAGE_PDF_PLUGIN
import com.sorrowblue.comicviewer.domain.usecase.invoke
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Scope
annotation class DocumentSheetOptionScope

@GraphExtension(DocumentSheetOptionScope::class)
interface DocumentSheetOptionContext {
    val getPdfPluginStateUseCase: GetPdfPluginStateUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun create(): DocumentSheetOptionContext
    }
}

@Composable
context(context: DocumentSheetOptionContext)
internal fun rememberDocumentSheetOptionState(): DocumentSheetOptionState {
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val state = remember {
        DocumentSheetOptionStateImpl(
            uriHandler = uriHandler,
            getPdfPluginStateUseCase = context.getPdfPluginStateUseCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        this.coroutineScope = coroutineScope
    }
    LifecycleResumeEffect(Unit) {
        state.onResume()
        onPauseOrDispose { }
    }
    return state
}

internal interface DocumentSheetOptionState {
    fun onOpenLinkClick()

    val uiState: DocumentSheetOptionUiState
}

private class DocumentSheetOptionStateImpl(
    private val uriHandler: UriHandler,
    private val getPdfPluginStateUseCase: GetPdfPluginStateUseCase,
    var coroutineScope: CoroutineScope,
) : DocumentSheetOptionState {
    override var uiState by mutableStateOf(DocumentSheetOptionUiState())

    init {
        extracted()
    }

    private fun extracted() {
        coroutineScope.launch {
            getPdfPluginStateUseCase().first()
                .onSuccess {
                    uiState = uiState.copy(pluginState = it)
                }
                .onError {
                }
        }
    }

    override fun onOpenLinkClick() {
        uriHandler.openUri("https://play.google.com/store/apps/details?id=$PACKAGE_PDF_PLUGIN")
    }

    fun onResume() {
        extracted()
    }
}

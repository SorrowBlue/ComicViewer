package com.sorrowblue.comicviewer.feature.library.box

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import org.koin.compose.koinInject

internal interface BoxLoginScreenState {
    fun onLoginClick()

    val uiState: BoxLoginScreenUiState
}

@Composable
internal fun rememberBoxLoginScreenState(
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
    repository: BoxApiRepository = koinInject(),
): BoxLoginScreenState {
    return remember {
        BoxLoginScreenStateImpl(
            savedStateHandle = savedStateHandle,
            context = context,
            repository = repository
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class BoxLoginScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    private val context: Context,
    private val repository: BoxApiRepository,
) : BoxLoginScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(BoxLoginScreenUiState()) }
        private set

    override fun onLoginClick() {
        CustomTabsIntent.Builder().build()
            .launchUrl(context, repository.getAuthorizationUrl().toUri())
    }
}

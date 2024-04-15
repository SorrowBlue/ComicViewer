package com.sorrowblue.comicviewer.feature.library.box

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.box.sdk.BoxAPIConnection
import java.net.URI
import kotlin.random.Random

internal interface BoxLoginScreenState {
    fun onLoginClick()

    val uiState: BoxLoginScreenUiState
}

@Composable
internal fun rememberBoxLoginScreenState(
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
): BoxLoginScreenState {
    return remember {
        BoxLoginScreenStateImpl(
            savedStateHandle = savedStateHandle,
            context = context
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class BoxLoginScreenStateImpl(
    savedStateHandle: SavedStateHandle,
    private val context: Context,
) : BoxLoginScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(BoxLoginScreenUiState()) }
        private set

    override fun onLoginClick() {
        val state = Random.nextInt(20).toString()
        val url = BoxAPIConnection.getAuthorizationURL(
            BuildConfig.BOX_CLIENT_ID,
            URI.create("https://comicviewer.sorrowblue.com/box/oauth2"),
            state,
            null
        )
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url.toString()))
    }
}

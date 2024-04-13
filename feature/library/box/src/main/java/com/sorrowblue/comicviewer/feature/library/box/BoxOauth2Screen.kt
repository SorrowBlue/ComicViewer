package com.sorrowblue.comicviewer.feature.library.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface BoxOauth2RouteNavigator {
    fun onComplete()
}

@Destination(
    navArgsDelegate = BoxOauth2Args::class,
    deepLinks = [
        DeepLink(uriPattern = "https://comicviewer.sorrowblue.com/box/oauth2?state={state}&code={code}")
    ]
)
@Composable
internal fun BoxOauth2Screen(
    args: BoxOauth2Args,
    navigator: BoxOauth2RouteNavigator,
) {
    BoxOauth2Screen(args = args, navigator::onComplete)
}

@Composable
private fun BoxOauth2Screen(
    args: BoxOauth2Args,
    onComplete: () -> Unit,
    state: BoxOauth2ScreenState = rememberBoxOauth2ScreenState(args),
) {
    BoxOauth2Screen()

    val onComplete1 by rememberUpdatedState(onComplete)
    LaunchedEffect(Unit) {
        state.authenticate(onComplete1)
    }
}

@Composable
private fun BoxOauth2Screen() {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Stable
internal class BoxOauth2ScreenState(
    private val args: BoxOauth2Args,
    private val repository: BoxApiRepository,
) : ViewModel() {

    fun authenticate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.authenticate(args.state, args.code, onSuccess)
        }
    }
}

@Composable
internal fun rememberBoxOauth2ScreenState(
    args: BoxOauth2Args,
    repository: BoxApiRepository = koinInject(),
) = remember {
    BoxOauth2ScreenState(args, repository)
}

class BoxOauth2Args(val state: String, val code: String)

package com.sorrowblue.comicviewer.feature.library.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.navigation.BoxGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

interface BoxOauth2RouteNavigator {
    fun onComplete()
    fun onFail()
}

@Destination<BoxGraph>(
    navArgs = BoxOauth2Args::class,
    deepLinks = [
        DeepLink(uriPattern = "https://comicviewer.sorrowblue.com/box/oauth2?state={state}&code={code}")
    ]
)
@Composable
internal fun BoxOauth2Screen(
    args: BoxOauth2Args,
    navigator: BoxOauth2RouteNavigator,
) {
    BoxOauth2Screen(args = args, navigator::onComplete, navigator::onFail)
}

@Composable
private fun BoxOauth2Screen(
    args: BoxOauth2Args,
    onComplete: () -> Unit,
    fail: () -> Unit,
    state: BoxOauth2ScreenState = rememberBoxOauth2ScreenState(args),
) {
    BoxOauth2Screen(state.snackbarHostState)
    val onComplete1 by rememberUpdatedState(onComplete)
    val fail1 by rememberUpdatedState(fail)
    LaunchedEffect(Unit) {
        state.authenticate(onComplete1, fail1)
    }
}

@Composable
private fun BoxOauth2Screen(
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
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
    val snackbarHostState: SnackbarHostState,
    private val args: BoxOauth2Args,
    private val scope: CoroutineScope,
    private val repository: BoxApiRepository,
) : ViewModel() {

    fun authenticate(onSuccess: () -> Unit, fail: () -> Unit) {
        viewModelScope.launch {
            delay(3000)
            repository.authenticate(args.state, args.code, onSuccess) {
                scope.launch {
                    snackbarHostState.showSnackbar("認証に失敗しました")
                    fail()
                }
            }
        }
    }
}

@Composable
internal fun rememberBoxOauth2ScreenState(
    args: BoxOauth2Args,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: BoxApiRepository = koinInject(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) = remember {
    BoxOauth2ScreenState(snackbarHostState, args, scope, repository)
}

data class BoxOauth2Args(val state: String, val code: String)

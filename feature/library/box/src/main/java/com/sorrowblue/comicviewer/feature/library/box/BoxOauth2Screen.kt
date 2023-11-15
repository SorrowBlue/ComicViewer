package com.sorrowblue.comicviewer.feature.library.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.navigation.BoxOauth2Args
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun BoxOauth2Route(
    args: BoxOauth2Args,
    onComplete: () -> Unit,
    state: BoxOauth2ScreenState = rememberBoxOauth2ScreenState(args),
) {
    BoxOauth2Screen()
    LaunchedEffect(Unit) {
        state.authenticate(onComplete)
    }
}

@Composable
private fun BoxOauth2Screen() {
    Scaffold {
        Box(modifier = Modifier.padding(it), contentAlignment = Alignment.Center) {
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

package com.sorrowblue.comicviewer.feature.library.onedrive

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper
import com.sorrowblue.comicviewer.feature.library.onedrive.data.AuthStatus
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import com.sorrowblue.comicviewer.feature.library.onedrive.data.oneDriveModule
import com.sorrowblue.comicviewer.feature.library.onedrive.dialog.OneDriveSignInDialog
import com.sorrowblue.comicviewer.feature.library.onedrive.navgraphs.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.ui.rememberKoinNavGraphModules
import org.koin.compose.koinInject

internal object OneDriveScreenWrapper : DestinationWrapper {
    @Composable
    override fun <T> DestinationScope<T>.Wrap(
        @SuppressLint("ComposableLambdaParameterNaming") screenContent: @Composable () -> Unit,
    ) {
        rememberKoinNavGraphModules(navController, OneDriveNavGraph.route) {
            listOf(oneDriveModule)
        }
        val repository = koinInject<OneDriveApiRepository>()
        val state by repository.authStatus.collectAsStateWithLifecycle()
        when (state) {
            AuthStatus.Uncertified -> OneDriveSignInDialog(onDismissRequest = destinationsNavigator::popBackStack)
            AuthStatus.Authenticated -> screenContent()
            AuthStatus.Undefined -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        LaunchedEffect(repository) {
            repository.initialize()
        }
        LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME, onEvent = repository::loadAccount)
    }
}

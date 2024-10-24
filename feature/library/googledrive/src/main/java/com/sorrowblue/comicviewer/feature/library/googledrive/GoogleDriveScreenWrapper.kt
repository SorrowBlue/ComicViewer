package com.sorrowblue.comicviewer.feature.library.googledrive

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.scope.DestinationScope
import com.ramcosta.composedestinations.wrapper.DestinationWrapper
import com.sorrowblue.comicviewer.feature.library.googledrive.data.AuthStatus
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleAuthorizationRepository
import com.sorrowblue.comicviewer.feature.library.googledrive.data.googleAuthModule
import com.sorrowblue.comicviewer.feature.library.googledrive.data.googleDriveModule
import com.sorrowblue.comicviewer.feature.library.googledrive.dialog.GoogleSignInDialog
import com.sorrowblue.comicviewer.feature.library.googledrive.navgraphs.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.framework.ui.rememberKoinNavGraphModules
import org.koin.compose.koinInject

internal object GoogleDriveScreenWrapper : DestinationWrapper {
    @Composable
    override fun <T> DestinationScope<T>.Wrap(
        @SuppressLint("ComposableLambdaParameterNaming") screenContent: @Composable () -> Unit,
    ) {
        rememberKoinNavGraphModules(navController, GoogleDriveNavGraph.route) {
            listOf(googleAuthModule, googleDriveModule)
        }
        val authRepository = koinInject<GoogleAuthorizationRepository>()
        val state by authRepository.state.collectAsStateWithLifecycle()
        when (state) {
            AuthStatus.Uncertified ->
                GoogleSignInDialog(onDismissRequest = {
                    destinationsNavigator.popBackStack(GoogleDriveNavGraph, true)
                })

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
    }
}

package com.sorrowblue.comicviewer.feature.library.googledrive.dialog

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.google.api.services.people.v1.PeopleServiceScopes
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleAuthorizationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal interface GoogleSignInDialogState {
    fun onSignInClick()
}

@Composable
internal fun rememberGoogleSignInDialogState(
    scope: CoroutineScope = rememberCoroutineScope(),
    authorizationRepository: GoogleAuthorizationRepository = koinInject(),
): GoogleSignInDialogState {
    return remember {
        GoogleSignInDialogStateImpl(
            scope = scope,
            repository = authorizationRepository
        )
    }.apply {
        resultLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = ::onResult
        )
    }
}

private class GoogleSignInDialogStateImpl(
    private val scope: CoroutineScope,
    private val repository: GoogleAuthorizationRepository,
) : GoogleSignInDialogState {

    lateinit var resultLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>

    fun onResult(activityResult: ActivityResult) {
        repository.authorizeResult(activityResult)
    }

    override fun onSignInClick() {
        scope.launch {
            repository.authorize(scopes, resultLauncher::launch)
        }
    }

    private val scopes = listOf(
        Scope(DriveScopes.DRIVE_READONLY),
        Scope(PeopleServiceScopes.USERINFO_PROFILE)
    )
}

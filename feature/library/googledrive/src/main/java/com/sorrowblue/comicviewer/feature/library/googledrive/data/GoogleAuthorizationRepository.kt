package com.sorrowblue.comicviewer.feature.library.googledrive.data

import android.app.PendingIntent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.Scope
import com.google.api.client.auth.oauth2.Credential
import kotlinx.coroutines.flow.StateFlow

internal enum class AuthStatus {
    Undefined,
    Uncertified,
    Authenticated,
}

internal interface GoogleAuthorizationRepository {

    val state: StateFlow<AuthStatus>

    suspend fun authorize(scopes: List<Scope>, launchIntent: (IntentSenderRequest) -> Unit)

    suspend fun signOut()

    fun authorizeResult(activityResult: ActivityResult)

    suspend fun <R> request(
        scopes: List<Scope>,
        authenticated: suspend (Credential) -> R,
        unauthorized: suspend (PendingIntent) -> R,
    ): R?
}

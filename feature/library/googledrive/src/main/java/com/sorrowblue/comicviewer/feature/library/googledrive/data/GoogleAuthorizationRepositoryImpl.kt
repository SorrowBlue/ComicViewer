package com.sorrowblue.comicviewer.feature.library.googledrive.data

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.drive.DriveScopes
import com.google.api.services.people.v1.PeopleServiceScopes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import org.koin.dsl.module

internal val googleAuthModule = module {
    single<GoogleAuthorizationRepository> { GoogleAuthorizationRepositoryImpl(get()) }
}

private class GoogleAuthorizationRepositoryImpl(
    private val context: Context,
) : GoogleAuthorizationRepository {

    override val state = MutableStateFlow(AuthStatus.Undefined)

    private val authorizationClient = Identity.getAuthorizationClient(context)
    private val credential = CredentialManager.create(context)

    init {
        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(scopes)
            .build()
        Identity.getAuthorizationClient(context).authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                if (authorizationResult.hasResolution()) {
                    state.value = AuthStatus.Uncertified
                } else {
                    state.value = AuthStatus.Authenticated
                }
            }
    }

    override suspend fun signOut() {
        credential.clearCredentialState(ClearCredentialStateRequest())
        request(scopes = scopes, authenticated = {}, unauthorized = {})
    }

    override suspend fun authorize(
        scopes: List<Scope>,
        launchIntent: (IntentSenderRequest) -> Unit,
    ) {
        request(
            scopes = scopes,
            authenticated = {
                logcat { "Already Authorize" }
            },
            unauthorized = {
                logcat { "Start Authorization UI" }
                val request = IntentSenderRequest.Builder(it).build()
                launchIntent(request)
            }
        )
    }

    override fun authorizeResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data != null) {
            runCatching {
                authorizationClient.getAuthorizationResultFromIntent(activityResult.data)
            }.onSuccess {
                if (it.hasResolution()) {
                    state.value = AuthStatus.Uncertified
                } else {
                    state.value = AuthStatus.Authenticated
                }
            }.onFailure {
                it.printStackTrace()
                if (it is ApiException) {
                    logcat("APP") { "認証に失敗しました。(${it.statusCode})" }
                } else if (it is RuntimeExecutionException && it.cause is ApiException) {
                    logcat("APP") { "認証に失敗しました。(${(it.cause as ApiException).statusCode})" }
                } else {
                    logcat("APP") { "エラーが発生しました。" }
                }
            }
        } else {
            logcat("APP") { "キャンセルしました。" }
        }
    }

    override suspend fun <R> request(
        scopes: List<Scope>,
        authenticated: suspend (Credential) -> R,
        unauthorized: suspend (PendingIntent) -> R,
    ): R? {
        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(scopes)
            .build()
        runCatching {
            Identity.getAuthorizationClient(context).authorize(authorizationRequest).await()
        }.fold({ authorizationResult ->
            if (authorizationResult.hasResolution()) {
                state.value = AuthStatus.Uncertified
                authorizationResult.pendingIntent?.let {
                    return unauthorized.invoke(it)
                }
            } else {
                state.value = AuthStatus.Authenticated
                authorizationResult.accessToken?.let {
                    val credential1 = Credential(BearerToken.authorizationHeaderAccessMethod())
                    credential1.accessToken = authorizationResult.accessToken
                    return authenticated.invoke(credential1)
                }
            }
        }, {
            logcat(priority = LogPriority.ERROR) { "Failed to authorize ${it.asLog()}" }
            throw it
        })
        return null
    }
}

private val scopes = listOf(
    Scope(DriveScopes.DRIVE_READONLY),
    Scope(PeopleServiceScopes.USERINFO_PROFILE)
)

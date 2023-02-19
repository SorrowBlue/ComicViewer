package com.sorrowblue.comicviewer.library.onedrive.data

import android.app.Activity
import android.content.Context
import com.microsoft.graph.authentication.BaseAuthenticationProvider
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.sorrowblue.comicviewer.library.onedrive.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.logcat

@Singleton
internal class AuthenticationProvider @Inject constructor(
    @ApplicationContext private val appContext: Context
) : BaseAuthenticationProvider() {

    private var clientApplication = MutableStateFlow<ISingleAccountPublicClientApplication?>(null)

    private val scopes = listOf("User.Read", "Files.Read")

    val isSingIned get() = clientApplication.value?.currentAccount?.currentAccount != null

    suspend fun initialize() {
        if (clientApplication.value != null) return
        withContext(Dispatchers.IO) {
            PublicClientApplication.createSingleAccountPublicClientApplication(
                appContext,
                R.raw.auth_config_single_account,
                object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                    override fun onCreated(application: ISingleAccountPublicClientApplication) {
                        logcat(LogPriority.INFO) { "Success creating MSAL application." }
                        clientApplication.value = application
                    }

                    override fun onError(exception: MsalException) {
                        logcat(LogPriority.ERROR) { "Error creating MSAL application. ${exception.localizedMessage}" }
                    }
                })
        }
    }

    override fun getAuthorizationTokenAsync(requestUrl: URL): CompletableFuture<String> {
        return if (shouldAuthenticateRequestWithUrl(requestUrl)) {
            runBlocking { acquireTokenSilently() }.thenApply { obj: IAuthenticationResult -> obj.accessToken }
        } else CompletableFuture.completedFuture(null)
    }

    val isAuthenticated = clientApplication.flatMapLatest{
        callbackFlow {
            if (it == null) {
                trySend(null).isSuccess
            } else {
                it.getCurrentAccountAsync(object :
                    ISingleAccountPublicClientApplication.CurrentAccountCallback {
                    override fun onAccountLoaded(activeAccount: IAccount?) {
                        trySend(activeAccount != null).isSuccess
                    }

                    override fun onAccountChanged(
                        priorAccount: IAccount?,
                        currentAccount: IAccount?
                    ) {
                        trySend(currentAccount != null).isSuccess
                    }

                    override fun onError(exception: MsalException) {
                        trySend( false).isSuccess
                    }
                })
            }
            awaitClose {  }
        }
    }

    val currentAccountFlow = clientApplication.filterNotNull().map{
        callbackFlow {
            it.getCurrentAccountAsync(object :
                ISingleAccountPublicClientApplication.CurrentAccountCallback {
                override fun onAccountLoaded(activeAccount: IAccount?) {
                    trySend(activeAccount).isSuccess
                }

                override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                    trySend(currentAccount).isSuccess
                }

                override fun onError(exception: MsalException) {
                    trySend(null).isSuccess
                }
            })
            awaitClose {  }
        }
    }


    suspend fun signIn(activity: Activity): CompletableFuture<IAuthenticationResult> {
        val future = CompletableFuture<IAuthenticationResult>()
//        https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/1742
//        val parameters = SignInParameters.builder()
//            .withActivity(activity)
//            .withLoginHint(null)
//            .withScopes(scopes)
//            .withCallback(getAuthenticationCallback(future))
//            .build()
//        clientApplication.signIn(parameters)
        clientApplication.value?.signIn(
            activity,
            null,
            scopes.toTypedArray(),
            getAuthenticationCallback(future)
        )
        return future
    }

    suspend fun signOut(): Unit? {
        return clientApplication.value?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                logcat(LogPriority.INFO) { "Signed out." }
            }

            override fun onError(exception: MsalException) {
                logcat(LogPriority.ERROR) { "MSAL error signing out. ${exception.localizedMessage}" }
            }
        })
    }

    private suspend fun acquireTokenSilently(): CompletableFuture<IAuthenticationResult> {
        val future = CompletableFuture<IAuthenticationResult>()
        val authority = clientApplication.value?.configuration?.defaultAuthority?.authorityURL?.toString()
            ?: return future
        clientApplication.value?.acquireTokenSilentAsync(
            scopes.toTypedArray(),
            authority,
            getAuthenticationCallback(future)
        )
        return future
    }

    private fun getAuthenticationCallback(future: CompletableFuture<IAuthenticationResult>) =
        object : AuthenticationCallback {
            override fun onCancel() {
                future.cancel(true)
            }

            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                future.complete(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                logcat { "${exception.localizedMessage}, errorCode=${exception.errorCode}" }
                future.completeExceptionally(exception)
            }
        }
}
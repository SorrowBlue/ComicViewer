package com.sorrowblue.comicviewer.feature.library.onedrive.data

import android.app.Activity
import android.content.Context
import com.microsoft.graph.authentication.BaseAuthenticationProvider
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication.CurrentAccountCallback
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException
import com.sorrowblue.comicviewer.feature.library.onedrive.R
import java.net.URL
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

internal class AuthenticationProvider(
    private val appContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BaseAuthenticationProvider() {

    private var clientApplication: ISingleAccountPublicClientApplication? = null
    val account = MutableStateFlow<IAccount?>(null)

    private val scopes = listOf("User.Read", "Files.Read")

    val isSignIned get() = clientApplication?.currentAccount?.currentAccount != null

    suspend fun initialize() {
        if (clientApplication != null) return
        withContext(dispatcher) {
            PublicClientApplication.createSingleAccountPublicClientApplication(
                appContext.applicationContext,
                R.raw.onedrive_auth_config_single_account,
                object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                    override fun onCreated(application: ISingleAccountPublicClientApplication) {
                        logcat(LogPriority.INFO) { "Success creating MSAL application." }
                        clientApplication = application
                        loadAccount()
                    }

                    override fun onError(exception: MsalException) {
                        logcat(LogPriority.ERROR) { "Error creating MSAL application. ${exception.localizedMessage}" }
                    }
                }
            )
        }
    }

    override fun getAuthorizationTokenAsync(requestUrl: URL): CompletableFuture<String> {
        return if (shouldAuthenticateRequestWithUrl(requestUrl)) {
            runBlocking { acquireTokenSilently() }.thenApply { obj: IAuthenticationResult -> obj.accessToken }
        } else {
            CompletableFuture.completedFuture(null)
        }
    }

    suspend fun signIn(activity: Activity): CompletableFuture<IAuthenticationResult> {
        val future = CompletableFuture<IAuthenticationResult>()
        val parameters = SignInParameters.builder()
            .withActivity(activity)
            .withLoginHint(null)
            .withScopes(scopes)
            .withCallback(
                getAuthenticationCallback(future) {
                    loadAccount()
                }
            )
            .build()
        withContext(dispatcher) {
            clientApplication?.signIn(parameters)
        }
        return future
    }

    suspend fun signOut(): Unit? {
        return withContext(dispatcher) {
            clientApplication?.signOut(object :
                ISingleAccountPublicClientApplication.SignOutCallback {
                override fun onSignOut() {
                    logcat(LogPriority.INFO) { "Signed out." }
                    account.value = null
                }

                override fun onError(exception: MsalException) {
                    logcat(LogPriority.ERROR) { "MSAL error signing out. ${exception.localizedMessage}" }
                }
            })
        }
    }

    private suspend fun acquireTokenSilently(): CompletableFuture<IAuthenticationResult> {
        val future = CompletableFuture<IAuthenticationResult>()
        val authority =
            clientApplication?.configuration?.defaultAuthority?.authorityURL?.toString()
                ?: return future
        // TODO(https://github.com/AzureAD/microsoft-authentication-library-for-android/issues/1742)
        // val silentParameters = AcquireTokenSilentParameters.Builder().fromAuthority(authority).withCallback(getAuthenticationCallback(future)).withScopes(scopes).build()
        // clientApplication.value?.acquireTokenSilentAsync(silentParameters)
        withContext(dispatcher) {
            @Suppress("DEPRECATION")
            clientApplication?.acquireTokenSilentAsync(
                scopes.toTypedArray(),
                authority,
                getAuthenticationCallback(future)
            )
        }
        return future
    }

    private fun getAuthenticationCallback(future: CompletableFuture<IAuthenticationResult>, onSuccess: () -> Unit = {}) =
        object : AuthenticationCallback {
            override fun onCancel() {
                logcat { "onCancel" }
                future.cancel(true)
            }

            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                logcat { "onSuccess" }
                onSuccess()
                future.complete(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                logcat { "${exception.localizedMessage}, errorCode=${exception.errorCode}" }
                future.completeExceptionally(exception)
            }
        }

    fun loadAccount() {
        clientApplication?.getCurrentAccountAsync(object : CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                logcat { "onAccountLoaded: ${activeAccount?.id}" }
                account.value = activeAccount
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                logcat { "onAccountChanged: priorAccount=${priorAccount?.id}, currentAccount=${currentAccount?.id}" }
                account.value = currentAccount
            }

            override fun onError(exception: MsalException) {
                logcat { "onError: ${exception.asLog()}" }
            }
        })
    }
}

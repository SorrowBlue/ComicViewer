package com.sorrowblue.comicviewer.feature.library.onedrive.data

import android.app.Activity
import android.content.Context
import com.azure.core.credential.AccessToken
import com.azure.core.credential.TokenCredential
import com.azure.core.credential.TokenRequestContext
import com.microsoft.identity.client.AcquireTokenSilentParameters
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
import java.time.ZoneOffset
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import reactor.core.publisher.Mono

internal enum class AuthStatus {
    Undefined,
    Uncertified,
    Authenticated,
}

internal class AuthenticationProvider(
    private val appContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TokenCredential {

    private var clientApplication: ISingleAccountPublicClientApplication? = null
    val account = MutableStateFlow<IAccount?>(null)
    val authStatus = MutableStateFlow(AuthStatus.Undefined)

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
                        logcat(TAG, LogPriority.INFO) { "Success creating MSAL application." }
                        clientApplication = application
                        loadAccount()
                    }

                    override fun onError(exception: MsalException) {
                        logcat(
                            TAG,
                            LogPriority.ERROR
                        ) { "Error creating MSAL application. ${exception.localizedMessage}" }
                    }
                }
            )
        }
    }

    override fun getToken(request: TokenRequestContext?): Mono<AccessToken> {
        return kotlin.runCatching {
            acquireTokenSilently2()?.let {
                AccessToken(it.accessToken, it.expiresOn.toInstant().atOffset(ZoneOffset.UTC))
            }
        }.getOrNull()?.let {
            Mono.just(it)
        } ?: Mono.empty()
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
                    logcat(TAG, LogPriority.INFO) { "Signed out." }
                    authStatus.value = AuthStatus.Uncertified
                    account.value = null
                }

                override fun onError(exception: MsalException) {
                    logcat(TAG, LogPriority.ERROR) { "MSAL error signing out. ${exception.localizedMessage}" }
                }
            })
        }
    }

    private fun acquireTokenSilently2(): IAuthenticationResult? {
        val authority = clientApplication?.configuration?.defaultAuthority?.authorityURL?.toString()
            ?: return null
        val silentParameters = AcquireTokenSilentParameters.Builder()
            .fromAuthority(authority)
            .forAccount(account.value)
            .withScopes(scopes)
            .build()
        return clientApplication?.acquireTokenSilent(silentParameters)
    }

    private fun getAuthenticationCallback(
        future: CompletableFuture<IAuthenticationResult>,
        onSuccess: () -> Unit = {},
    ) =
        object : AuthenticationCallback {
            override fun onCancel() {
                logcat("TAG") { "onCancel" }
                future.cancel(true)
            }

            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                logcat("TAG") { "onSuccess ${authenticationResult.account.id}" }
                onSuccess()
                future.complete(authenticationResult)
            }

            override fun onError(exception: MsalException) {
                logcat("TAG") { "${exception.localizedMessage}, errorCode=${exception.errorCode}" }
                future.completeExceptionally(exception)
            }
        }

    fun loadAccount() {
        clientApplication?.getCurrentAccountAsync(object : CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                logcat("TAG") { "onAccountLoaded: ${activeAccount?.id}" }
                authStatus.value = if (activeAccount == null) AuthStatus.Uncertified else AuthStatus.Authenticated
                account.value = activeAccount
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                logcat(
                    "TAG"
                ) { "onAccountChanged: priorAccount=${priorAccount?.id}, currentAccount=${currentAccount?.id}" }
                authStatus.value = if (currentAccount == null) AuthStatus.Uncertified else AuthStatus.Authenticated
                account.value = currentAccount
            }

            override fun onError(exception: MsalException) {
                logcat("TAG") { "onError: ${exception.asLog()}" }
            }
        })
    }
}

private const val TAG = "AuthenticationProvider"

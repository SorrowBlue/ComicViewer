package com.sorrowblue.comicviewer.library.onedrive.signin

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.library.onedrive.data.AuthenticationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import logcat.logcat

internal class OneDriveSignInViewModel(application: Application) : AndroidViewModel(application) {

    private val authenticationProvider: AuthenticationProvider = AuthenticationProvider.getInstance(application)

    val loginsState = MutableStateFlow(SignInState.NONE)

    fun signIn(activity: Activity, done: () -> Unit) {
        loginsState.value = SignInState.RUNNNING
        viewModelScope.launch {
            kotlin.runCatching {
                authenticationProvider.signIn(activity).await()
            }.onSuccess {
                logcat { "success signin. id=${it.account.id}" }
                if (it.account.idToken != null) {
                    loginsState.value = SignInState.SIGNINED
                    done()
                } else {
                    loginsState.value = SignInState.ERROR
                }
            }.onFailure {
                it.printStackTrace()
                loginsState.value = SignInState.ERROR
            }
        }
    }
}

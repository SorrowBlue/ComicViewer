package com.sorrowblue.comicviewer.library.onedrive.signin

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.library.onedrive.data.AuthenticationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch

@HiltViewModel
internal class OneDriveSignInViewModel @Inject constructor(private val authenticationProvider: AuthenticationProvider) :
    ViewModel() {

    val loginsState = MutableStateFlow(SignInState.NONE)

    fun signIn(activity: Activity, done: () -> Unit) {
        loginsState.value = SignInState.RUNNNING
        viewModelScope.launch {
            kotlin.runCatching {
                authenticationProvider.signIn(activity).await()
            }.onSuccess {
                if (it.account.idToken != null) {
                    loginsState.value = SignInState.SIGNINED
                    done()
                } else {
                    loginsState.value = SignInState.ERROR
                }
            }.onFailure {
                loginsState.value = SignInState.ERROR
            }
        }
    }
}

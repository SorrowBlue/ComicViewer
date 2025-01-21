package com.sorrowblue.comicviewer.feature.authentication

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine

internal actual class BiometricManager(
    private val context: Context,
) {
    actual suspend fun authenticate(): AuthenticationResult =
        suspendCancellableCoroutine { continuation ->
            val biometricPrompt = BiometricPrompt(
                context as FragmentActivity,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        continuation.resume(AuthenticationResult.Success) { cause, _, _ -> }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        continuation.resume(AuthenticationResult.Failed) { cause, _, _ -> }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        continuation.resume(AuthenticationResult.Error(errString.toString())) { cause, _, _ -> }
                    }
                }
            )
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setTitle(context.getString(R.string.authentication_title_fingerprint_auth))
                .setNegativeButtonText("Cancel TODO")
                .build()
            biometricPrompt.authenticate(promptInfo)
        }
}

@Composable
internal actual fun rememberBiometricManager(): com.sorrowblue.comicviewer.feature.authentication.BiometricManager {
    val context = LocalContext.current
    return remember { BiometricManager(context) }
}

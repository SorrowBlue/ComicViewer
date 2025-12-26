package com.sorrowblue.comicviewer.feature.authentication

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_biometric_cancel
import comicviewer.feature.authentication.generated.resources.authentication_title_fingerprint_auth
import kotlinx.coroutines.suspendCancellableCoroutine
import logcat.logcat
import org.jetbrains.compose.resources.getString

internal actual class BiometricManager(private val context: Context) {
    actual suspend fun authenticate(): AuthenticationResult {
        val title = getString(Res.string.authentication_title_fingerprint_auth)
        val cancelText = getString(Res.string.authentication_biometric_cancel)
        return suspendCancellableCoroutine { continuation ->
            val biometricPrompt = BiometricPrompt(
                context as FragmentActivity,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult,
                    ) {
                        super.onAuthenticationSucceeded(result)
                        logcat { "onAuthenticationSucceeded result=$result" }
                        continuation.resume(AuthenticationResult.Success) { cause, _, _ -> }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        logcat { "onAuthenticationFailed" }
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        logcat { "onAuthenticationError errorCode=$errorCode errString=$errString" }
                        super.onAuthenticationError(errorCode, errString)
                        continuation.resume(
                            AuthenticationResult.Error(errString.toString()),
                        ) { cause, _, _ -> }
                    }
                },
            )
            val promptInfo = BiometricPrompt.PromptInfo
                .Builder()
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setTitle(title)
                .setNegativeButtonText(cancelText)
                .build()
            biometricPrompt.authenticate(promptInfo)
        }
    }
}

@Composable
internal actual fun rememberBiometricManager(): com.sorrowblue.comicviewer.feature.authentication.BiometricManager {
    val context = LocalContext.current
    return remember { BiometricManager(context) }
}

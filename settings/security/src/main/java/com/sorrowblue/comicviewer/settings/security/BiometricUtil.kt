package com.sorrowblue.comicviewer.settings.security

import androidx.biometric.BiometricManager

fun <R> BiometricManager.check(
    onSuccess: () -> R,
    noneEnrolled: () -> R,
    notSupported: (Int) -> R
): R {
    return when (val state = canAuthenticateWeak()) {
        BiometricManager.BIOMETRIC_SUCCESS -> onSuccess()
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> noneEnrolled()
        else -> notSupported(state)
    }
}

fun BiometricManager.canAuthenticateWeak() =
    canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)

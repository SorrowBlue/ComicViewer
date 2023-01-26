package com.sorrowblue.comicviewer.settings.security

import android.os.Build
import androidx.biometric.BiometricManager

object BiometricUtil {

    val authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK
}

fun <R> BiometricManager.check(
    onSuccess: () -> R,
    noneEnrolled: () -> R,
    notSupported: (Int) -> R
): R {
    return when (val state = canAuthenticate(BiometricUtil.authenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> onSuccess()
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> noneEnrolled()
        else -> notSupported(state)
    }
}

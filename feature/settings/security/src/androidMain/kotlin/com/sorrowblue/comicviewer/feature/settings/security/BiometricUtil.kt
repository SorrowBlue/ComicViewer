package com.sorrowblue.comicviewer.feature.settings.security

import androidx.annotation.IntDef
import androidx.biometric.BiometricManager

@AuthenticationStatus
fun BiometricManager.canAuthenticateWeak(): Int =
    canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)

@Target(AnnotationTarget.FUNCTION)
@IntDef(
    BiometricManager.BIOMETRIC_SUCCESS,
    BiometricManager.BIOMETRIC_STATUS_UNKNOWN,
    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
)
@Retention(AnnotationRetention.SOURCE)
annotation class AuthenticationStatus

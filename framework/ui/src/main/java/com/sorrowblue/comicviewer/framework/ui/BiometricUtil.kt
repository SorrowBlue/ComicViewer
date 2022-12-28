package com.sorrowblue.comicviewer.framework.ui

import android.os.Build
import androidx.biometric.BiometricManager

object BiometricUtil {

    val authenticators = if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    } else {
        BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    }
}

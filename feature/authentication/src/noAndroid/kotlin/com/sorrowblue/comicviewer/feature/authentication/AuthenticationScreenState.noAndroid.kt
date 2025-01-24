package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

internal actual class BiometricManager {
    actual suspend fun authenticate(): AuthenticationResult {
        TODO("Not yet implemented")
    }
}
@Composable
internal actual fun rememberBiometricManager(): BiometricManager {
    return remember { BiometricManager() }
}

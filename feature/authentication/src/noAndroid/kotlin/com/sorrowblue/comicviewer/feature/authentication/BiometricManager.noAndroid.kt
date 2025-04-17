package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal actual fun rememberBiometricManager(): BiometricManager {
    return remember { BiometricManager() }
}

internal actual class BiometricManager {
    actual suspend fun authenticate(): AuthenticationResult {
        // FEATURE Android以外では生体認証はサポートしない
        return AuthenticationResult.Error("not support")
    }
}

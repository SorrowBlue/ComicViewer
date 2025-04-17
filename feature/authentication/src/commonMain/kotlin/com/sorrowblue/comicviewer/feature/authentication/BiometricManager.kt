package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.runtime.Composable

@Composable
internal expect fun rememberBiometricManager(): BiometricManager

internal expect class BiometricManager {
    suspend fun authenticate(): AuthenticationResult
}

internal sealed interface AuthenticationResult {
    data object Success : AuthenticationResult
    data class Error(val message: String) : AuthenticationResult
}

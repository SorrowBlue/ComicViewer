/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.first

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class AuthenticationViewModel(
    private val securitySettings: ManageSecuritySettingsUseCase,
) : ViewModel() {
    /**
     * Validates if the provided PIN meets minimum requirements.
     */
    fun validatePinLength(pin: String): Boolean = pin.length >= MIN_PIN_SIZE

    /**
     * Verifies if the provided PIN matches the stored password.
     */
    suspend fun verifyPin(pin: String): Boolean = pin == securitySettings.settings.first().password

    /**
     * Saves the new PIN to security settings.
     */
    suspend fun savePin(pin: String) {
        securitySettings.edit { it.copy(password = pin) }
    }

    /**
     * Removes the PIN and disables biometrics from security settings.
     */
    suspend fun removePin() {
        securitySettings.edit { it.copy(password = null, useBiometrics = false) }
    }

    /**
     * Checks if biometric authentication is enabled in security settings.
     */
    suspend fun isBiometricsEnabled(): Boolean = securitySettings.settings.first().useBiometrics

    companion object {
        const val MIN_PIN_SIZE = 4
    }
}

/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dev.zacsweers.metrox.viewmodel.metroViewModel

internal interface SecuritySettingsScreenState {
    fun onChangeBackgroundLockEnabled(value: Boolean)

    fun onChangeBiometricEnabled(value: Boolean)

    fun onResume()

    fun onBiometricsDialogClick()

    fun onBiometricsDialogDismissRequest()

    val snackbarHostState: SnackbarHostState
    val uiState: SecuritySettingsScreenUiState
}

@Composable
internal expect fun rememberSecuritySettingsScreenState(
    viewModel: SecuritySettingsViewModel = metroViewModel(),
): SecuritySettingsScreenState

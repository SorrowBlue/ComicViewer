package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import comicviewer.feature.settings.security.generated.resources.Res
import comicviewer.feature.settings.security.generated.resources.settings_security_label_background_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_summary_password_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_summary_use_biometric_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_title
import comicviewer.feature.settings.security.generated.resources.settings_security_title_change_password
import comicviewer.feature.settings.security.generated.resources.settings_security_title_password_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_title_use_biometric_auth
import org.jetbrains.compose.resources.stringResource

internal data class SecuritySettingsScreenUiState(
    val isAuthEnabled: Boolean = false,
    val isBiometricCanBeUsed: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBackgroundLockEnabled: Boolean = false,
    val isBiometricsDialogShow: Boolean = false,
)

@Composable
internal fun SecuritySettingsScreen(
    uiState: SecuritySettingsScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChangeAuthEnable: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onChangeBiometricEnable: (Boolean) -> Unit,
    onChangeBackgroundLockEnable: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_security_title)) },
        onBackClick = onBackClick,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) {
        SwitchSetting(
            title = Res.string.settings_security_title_password_lock,
            checked = uiState.isAuthEnabled,
            onCheckedChange = onChangeAuthEnable,
            summary = Res.string.settings_security_summary_password_lock,
        )
        Setting(
            title = Res.string.settings_security_title_change_password,
            onClick = onPasswordChangeClick,
            enabled = uiState.isAuthEnabled,
        )
        if (uiState.isBiometricCanBeUsed) {
            SwitchSetting(
                title = Res.string.settings_security_title_use_biometric_auth,
                checked = uiState.isBiometricEnabled,
                onCheckedChange = onChangeBiometricEnable,
                summary = Res.string.settings_security_summary_use_biometric_auth,
                enabled = uiState.isAuthEnabled,
            )
        }
        SwitchSetting(
            title = Res.string.settings_security_label_background_lock,
            checked = uiState.isBackgroundLockEnabled,
            onCheckedChange = onChangeBackgroundLockEnable,
            enabled = uiState.isAuthEnabled,
        )
    }
}

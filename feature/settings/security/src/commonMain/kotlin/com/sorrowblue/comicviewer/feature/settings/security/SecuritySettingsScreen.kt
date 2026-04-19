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
import comicviewer.feature.settings.security.generated.resources.settings_security_label_app_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_label_biometric_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_label_change_password
import comicviewer.feature.settings.security.generated.resources.settings_security_label_lock_background
import comicviewer.feature.settings.security.generated.resources.settings_security_summary_app_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_summary_biometric_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_title
import org.jetbrains.compose.resources.stringResource

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
            title = {
                Text(stringResource(Res.string.settings_security_label_app_lock))
            },
            summary = {
                Text(stringResource(Res.string.settings_security_summary_app_lock))
            },
            checked = uiState.isAuthEnabled,
            onCheckedChange = onChangeAuthEnable,
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_security_label_change_password))
            },
            onClick = onPasswordChangeClick,
            enabled = uiState.isAuthEnabled,
        )
        if (uiState.isBiometricCanBeUsed) {
            SwitchSetting(
                title = {
                    Text(stringResource(Res.string.settings_security_label_biometric_auth))
                },
                summary = {
                    Text(stringResource(Res.string.settings_security_summary_biometric_auth))
                },
                checked = uiState.isBiometricEnabled,
                onCheckedChange = onChangeBiometricEnable,
                enabled = uiState.isAuthEnabled,
            )
        }
        SwitchSetting(
            title = {
                Text(stringResource(Res.string.settings_security_label_lock_background))
            },
            checked = uiState.isBackgroundLockEnabled,
            onCheckedChange = onChangeBackgroundLockEnable,
            enabled = uiState.isAuthEnabled,
        )
    }
}

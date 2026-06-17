package com.sorrowblue.comicviewer.feature.settings.security

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.security.generated.resources.Res
import comicviewer.feature.settings.security.generated.resources.settings_security_label_app_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_label_biometric_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_label_change_password
import comicviewer.feature.settings.security.generated.resources.settings_security_label_lock_background
import comicviewer.feature.settings.security.generated.resources.settings_security_summary_app_lock
import comicviewer.feature.settings.security.generated.resources.settings_security_summary_biometric_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_title
import org.jetbrains.compose.resources.stringResource

@NavDestination(SecuritySettingsNavKey::class)
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

@NavPreview(SecuritySettingsNavKey::class, primary = true)
@Preview
@Composable
private fun SecuritySettingsScreenPreview() = PreviewTheme {
    SecuritySettingsScreen(
        uiState = SecuritySettingsScreenUiState(
            isAuthEnabled = true,
            isBiometricCanBeUsed = true,
            isBiometricEnabled = true,
            isBackgroundLockEnabled = true,
        ),
        snackbarHostState = SnackbarHostState(),
        onBackClick = {},
        onChangeAuthEnable = {},
        onPasswordChangeClick = {},
        onChangeBiometricEnable = {},
        onChangeBackgroundLockEnable = {},
    )
}

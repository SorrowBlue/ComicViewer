package com.sorrowblue.comicviewer.feature.settings.security.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.settings.security.R
import com.sorrowblue.comicviewer.framework.compose.material3.ListItem2
import com.sorrowblue.comicviewer.framework.compose.material3.ListItemSwitch

internal data class SettingsSecuritySheetUiState(
    val isAuthEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBackgroundLockEnabled: Boolean = false
)

@Composable
internal fun SettingsSecuritySheet(
    uiState: SettingsSecuritySheetUiState,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onChangeBiometricEnabled: (Boolean) -> Unit,
    onChangeBackgroundLockEnabled: (Boolean) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        ListItemSwitch(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_security_title_password_lock))
            },
            checked = uiState.isAuthEnabled,
            onCheckedChange = onChangeAuthEnabled,
            supportingContent = {
                Text(text = stringResource(id = R.string.settings_security_summary_password_lock))
            },
        )
        ListItem2(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_security_title_change_password))
            },
            enabled = uiState.isAuthEnabled,
            modifier = Modifier.clickable(
                enabled = uiState.isAuthEnabled,
                onClick = onPasswordChangeClick
            ),
        )
        ListItemSwitch(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_security_title_use_biometric_auth))
            },
            enabled = uiState.isAuthEnabled,
            checked = uiState.isBiometricEnabled,
            onCheckedChange = onChangeBiometricEnabled,
            supportingContent = {
                Text(text = stringResource(id = R.string.settings_security_summary_use_biometric_auth))
            },
        )
        ListItemSwitch(
            headlineContent = {
                Text(text = stringResource(id = R.string.settings_security_label_background_lock))
            },
            enabled = uiState.isAuthEnabled,
            checked = uiState.isBackgroundLockEnabled,
            onCheckedChange = onChangeBackgroundLockEnabled,
        )
    }
}

package com.sorrowblue.comicviewer.settings.security

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.CollectAsEffect
import com.sorrowblue.comicviewer.settings.security.component.SettingsFolderTopAppBar
import com.sorrowblue.comicviewer.settings.security.section.BiometricsDialog
import com.sorrowblue.comicviewer.settings.security.section.BiometricsRequestDialogUiState
import com.sorrowblue.comicviewer.settings.security.section.PasswordDialog
import com.sorrowblue.comicviewer.settings.security.section.PasswordDialogUiState
import com.sorrowblue.comicviewer.settings.security.section.SettingsSecuritySheet
import com.sorrowblue.comicviewer.settings.security.section.SettingsSecuritySheetUiState

internal data class SettingsSecurityScreenUiState(
    val settingsSecuritySheetUiState: SettingsSecuritySheetUiState = SettingsSecuritySheetUiState(),
    val biometricsRequestDialogUiState: BiometricsRequestDialogUiState = BiometricsRequestDialogUiState.Hide,
    val passwordDialogUiState: PasswordDialogUiState = PasswordDialogUiState.Hide,
)

sealed interface SettingsSecurityUiEvent {

    sealed interface Message : SettingsSecurityUiEvent {
        data class Text(val text: String) : Message
        data class Resource(private val resId: Int) : Message {
            fun text(context: Context) = ContextCompat.getString(context, resId)
        }
    }

    data class BiometricPrompt(val enabled: Boolean) : SettingsSecurityUiEvent
}

@Composable
internal fun SettingsSecurityRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsSecurityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val activity = context as FragmentActivity
    val resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { viewModel.onResult() }
    )
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, viewModel) {
        lifecycle.addObserver(viewModel)
        onDispose { lifecycle.removeObserver(viewModel) }
    }
    viewModel.uiEvents.CollectAsEffect {
        when (it) {
            is SettingsSecurityUiEvent.BiometricPrompt -> {
                val biometricPrompt = BiometricPrompt(activity, viewModel.authenticationCallback)
                val info = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("アプリ内の生体認証")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    .setSubtitle("アプリ内で生体認証を${if (it.enabled) "有効化" else "無効化"}するために、認証してください。")
                    .setNegativeButtonText("Cancel")
                    .build()
                biometricPrompt.authenticate(info)
            }

            is SettingsSecurityUiEvent.Message.Resource ->
                snackbarHostState.showSnackbar(it.text(context))

            is SettingsSecurityUiEvent.Message.Text ->
                snackbarHostState.showSnackbar(it.text)

        }
    }

    SettingsSecurityScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onChangeAuthEnabled = viewModel::onChangeAuthEnabled,
        onPasswordChangeClick = viewModel::onPasswordChangeClick,
        onChangeBiometricEnabled = viewModel::onChangeBiometricEnabled,
        onChangeBackgroundLockEnabled = viewModel::onChangeBackgroundLockEnabled,
        onPasswordChange = viewModel::onPasswordChange,
        onOldPasswordChange = viewModel::onOldPasswordChange,
        onPasswordDialogConfirmClick = viewModel::onPasswordDialogConfirmClick,
        onPasswordDialogDismissRequest = viewModel::onPasswordDialogDismissRequest,
        onBiometricsSettingsClick = {
            viewModel.onBiometricsRequestDialogDismissRequest()
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
            }
            resultLauncher.launch(enrollIntent)
        },
        onBiometricsRequestDialogDismissRequest = viewModel::onBiometricsRequestDialogDismissRequest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSecurityScreen(
    uiState: SettingsSecurityScreenUiState = SettingsSecurityScreenUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit = {},
    onChangeAuthEnabled: (Boolean) -> Unit = {},
    onPasswordChangeClick: () -> Unit = {},
    onChangeBiometricEnabled: (Boolean) -> Unit = {},
    onChangeBackgroundLockEnabled: (Boolean) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onOldPasswordChange: (String) -> Unit = {},
    onPasswordDialogConfirmClick: () -> Unit = {},
    onPasswordDialogDismissRequest: () -> Unit = {},
    onBiometricsSettingsClick: () -> Unit = {},
    onBiometricsRequestDialogDismissRequest: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SettingsFolderTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsSecuritySheet(
            uiState = uiState.settingsSecuritySheetUiState,
            onChangeAuthEnabled = onChangeAuthEnabled,
            onPasswordChangeClick = onPasswordChangeClick,
            onChangeBiometricEnabled = onChangeBiometricEnabled,
            onChangeBackgroundLockEnabled = onChangeBackgroundLockEnabled,
            contentPadding = contentPadding
        )

        BiometricsDialog(
            uiState = uiState.biometricsRequestDialogUiState,
            onNextClick = onBiometricsSettingsClick,
            onDismissRequest = onBiometricsRequestDialogDismissRequest
        )
        PasswordDialog(
            uiState = uiState.passwordDialogUiState,
            onPasswordChange = onPasswordChange,
            onOldPasswordChange = onOldPasswordChange,
            onConfirmClick = onPasswordDialogConfirmClick,
            onDismissRequest = onPasswordDialogDismissRequest
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsFolderScreen() {
    AppMaterialTheme {
        Surface {
            SettingsSecurityScreen()
        }
    }
}

package com.sorrowblue.comicviewer.feature.settings.security

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import comicviewer.feature.settings.security.generated.resources.Res
import comicviewer.feature.settings.security.generated.resources.settings_security_msg_desabled_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_msg_disabled_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_msg_enabled_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_msg_temporarily_unavailable_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_not_available_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_text_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_text_disable_bio_auth
import comicviewer.feature.settings.security.generated.resources.settings_security_title_bio_auth
import comicviewer.framework.ui.generated.resources.cancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString
import comicviewer.framework.ui.generated.resources.Res as UiRes

@Composable
internal actual fun rememberSecuritySettingsScreenState(
    scope: CoroutineScope,
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
): SecuritySettingsScreenState {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val state = remember {
        SecuritySettingsScreenStateImpl(
            context = context,
            scope = scope,
            manageSecuritySettingsUseCase = manageSecuritySettingsUseCase,
            snackbarHostState = snackbarHostState
        )
    }
    state.resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { state.activityResult() }
    )
    return state
}

private class SecuritySettingsScreenStateImpl(
    private val context: Context,
    private val scope: CoroutineScope,
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    override val snackbarHostState: SnackbarHostState,
) : SecuritySettingsScreenState {

    private val biometricManager = BiometricManager.from(context)

    lateinit var resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    override var uiState by mutableStateOf(SecuritySettingsScreenUiState())

    init {
        manageSecuritySettingsUseCase.settings.onEach {
            uiState = uiState.copy(
                isAuthEnabled = it.password != null,
                isBackgroundLockEnabled = it.lockOnBackground,
                isBiometricEnabled = it.useBiometrics
            )
        }.launchIn(scope)
        val state = BiometricManager.from(
            context
        ).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        val isEnabled = state == BiometricManager.BIOMETRIC_SUCCESS || // OK
            state == BiometricManager.BIOMETRIC_STATUS_UNKNOWN || // 判断不可
            state == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED // 未登録
        uiState = uiState.copy(isBiometricCanBeUsed = isEnabled)
    }

    override fun onBiometricsDialogClick() {
        onBiometricsDialogDismissRequest()
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
        }
        resultLauncher.launch(enrollIntent)
    }

    fun activityResult() {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                startBiometric()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                logcat { "認証 Not 有効化 リトライ" }
                scope.launch {
                    snackbarHostState.showSnackbar(getString(Res.string.settings_security_msg_desabled_bio_auth))
                }
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN,
            -> {
                logcat { "生体認証 利用不可" }
                scope.launch {
                    snackbarHostState.showSnackbar(getString(Res.string.settings_security_not_available_bio_auth))
                }
            }

            BiometricManager.BIOMETRIC_ERROR_NOT_ENABLED_FOR_APPS -> {
                TODO()
            }
        }
    }

    private fun startBiometric() {
        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    logcat { "Authentication error: $errString" }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    logcat { "Authentication succeeded!" }
                    logcat { "整体認証を有効にする" }
                    scope.launch {
                        manageSecuritySettingsUseCase.edit {
                            it.copy(useBiometrics = true)
                        }
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar(getString(Res.string.settings_security_msg_enabled_bio_auth))
                    }
                }
            }
        )
        scope.launch {
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(Res.string.settings_security_title_bio_auth))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setSubtitle(getString(Res.string.settings_security_text_bio_auth))
                .setNegativeButtonText(getString(UiRes.string.cancel))
                .build()
            biometricPrompt.authenticate(info)
        }
    }

    override fun onChangeBackgroundLockEnabled(value: Boolean) {
        scope.launch {
            manageSecuritySettingsUseCase.edit {
                it.copy(lockOnBackground = true)
            }
        }
    }

    override fun onChangeBiometricEnabled(value: Boolean) {
        if (value) {
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS, BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                    // 生体認証が有効なため、認証する
                    startBiometric()
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // 生体認証が設定されていないため、設定を促す
                    uiState = uiState.copy(isBiometricsDialogShow = true)
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    // 生体認証が一時的に利用不可のため、エラーメッセージ表示
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            getString(Res.string.settings_security_msg_temporarily_unavailable_bio_auth)
                        )
                    }
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                -> {
                    // 生体認証が利用不可のため、エラーメッセージ表示
                    scope.launch {
                        snackbarHostState.showSnackbar(getString(Res.string.settings_security_not_available_bio_auth))
                    }
                }
            }
        } else {
            // 生体認証を無効化したいので認証する。
            val biometricPrompt = BiometricPrompt(
                context as FragmentActivity,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        logcat { "Authentication error: $errString" }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        logcat { "Authentication succeeded!" }
                        logcat { "整体認証を無効にする" }
                        scope.launch {
                            manageSecuritySettingsUseCase.edit {
                                it.copy(useBiometrics = false)
                            }
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                getString(Res.string.settings_security_msg_disabled_bio_auth)
                            )
                        }
                    }
                }
            )
            scope.launch {
                val info = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(Res.string.settings_security_title_bio_auth))
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    .setSubtitle(getString(Res.string.settings_security_text_disable_bio_auth))
                    .setNegativeButtonText(getString(UiRes.string.cancel))
                    .build()
                biometricPrompt.authenticate(info)
            }
        }
    }

    override fun onResume() {
        scope.launch {
            if (manageSecuritySettingsUseCase.settings.first().useBiometrics) {
                when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
                    ->
                        scope.launch {
                            manageSecuritySettingsUseCase.edit {
                                it.copy(useBiometrics = false)
                            }
                        }

                    BiometricManager.BIOMETRIC_SUCCESS, BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> Unit
                }
            }
        }
    }

    override fun onBiometricsDialogDismissRequest() {
        uiState = uiState.copy(isBiometricsDialogShow = false)
    }
}

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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

@Stable
internal interface SecuritySettingsScreenState {
    fun activityResult(activityResult: ActivityResult)
    fun onChangeBackgroundLockEnabled(value: Boolean)
    fun onChangeBiometricEnabled(value: Boolean)
    fun onResume()
    fun onBiometricsDialogClick()

    fun onBiometricsDialogDismissRequest()
    val snackbarHostState: SnackbarHostState
    var uiState: SecuritySettingsScreenUiState
}

@Composable
internal fun rememberSecuritySettingsScreenState(
    state: SecuritySettingsScreenState = rememberChildSecuritySettingsScreenState(),
    resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = state::activityResult
    ),
): SecuritySettingsScreenState = remember {
    SecuritySettingsScreenStateImpl(state = state, resultLauncher = resultLauncher)
}

private class SecuritySettingsScreenStateImpl(
    private val resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    val state: SecuritySettingsScreenState,
) : SecuritySettingsScreenState by state {
    override fun onBiometricsDialogClick() {
        state.onBiometricsDialogDismissRequest()
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
        }
        resultLauncher.launch(enrollIntent)
    }
}

@Composable
internal fun rememberChildSecuritySettingsScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SecuritySettingsViewModel = koinViewModel(),
): SecuritySettingsScreenState = remember {
    ChildSecuritySettingsScreenState(context = context, scope = scope, viewModel = viewModel)
}

private class ChildSecuritySettingsScreenState(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: SecuritySettingsViewModel,
    override val snackbarHostState: SnackbarHostState = SnackbarHostState(),
) : SecuritySettingsScreenState {

    private val biometricManager = BiometricManager.from(context)

    override fun onBiometricsDialogClick() {
        throw NotImplementedError("")
    }

    override fun activityResult(activityResult: ActivityResult) {
        when (biometricManager.canAuthenticateWeak()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                startBiometric()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                logcat { "認証 Not 有効化 リトライ" }
                scope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.settings_security_msg_desabled_bio_auth))
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
                    snackbarHostState.showSnackbar(context.getString(R.string.settings_security_not_available_bio_auth))
                }
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
                    viewModel.updateUseBiometrics(true)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.settings_security_msg_enabled_bio_auth)
                        )
                    }
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.settings_security_title_bio_auth))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setSubtitle(context.getString(R.string.settings_security_text_bio_auth))
            .setNegativeButtonText(context.getString(android.R.string.cancel))
            .build()
        biometricPrompt.authenticate(info)
    }

    override fun onChangeBackgroundLockEnabled(value: Boolean) {
        viewModel.updateLockOnBackground(value)
    }

    override fun onChangeBiometricEnabled(value: Boolean) {
        if (value) {
            when (biometricManager.canAuthenticateWeak()) {
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
                            context.getString(R.string.settings_security_msg_temporarily_unavailable_bio_auth)
                        )
                    }
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                -> {
                    // 生体認証が利用不可のため、エラーメッセージ表示
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.settings_security_not_available_bio_auth)
                        )
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
                        viewModel.updateUseBiometrics(false)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.settings_security_msg_disabled_bio_auth)
                            )
                        }
                    }
                }
            )
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.settings_security_title_bio_auth))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setSubtitle(context.getString(R.string.settings_security_text_disable_bio_auth))
                .setNegativeButtonText(context.getString(android.R.string.cancel))
                .build()
            biometricPrompt.authenticate(info)
        }
    }

    override fun onResume() {
        scope.launch {
            if (viewModel.securitySettings.first().useBiometrics) {
                when (biometricManager.canAuthenticateWeak()) {
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
                    -> viewModel.updateUseBiometrics(false)

                    BiometricManager.BIOMETRIC_SUCCESS, BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> Unit
                }
            }
        }
    }

    override fun onBiometricsDialogDismissRequest() {
        uiState = uiState.copy(isBiometricsDialogShow = false)
    }

    override var uiState by mutableStateOf(SecuritySettingsScreenUiState())

    init {
        viewModel.securitySettings.onEach {
            uiState = uiState.copy(
                isAuthEnabled = it.password != null,
                isBackgroundLockEnabled = it.lockOnBackground,
                isBiometricEnabled = it.useBiometrics
            )
        }.launchIn(scope)
    }
}

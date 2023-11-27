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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.security.section.BiometricsDialog
import com.sorrowblue.comicviewer.framework.ui.DialogController
import com.sorrowblue.comicviewer.framework.ui.LifecycleResumeEffect
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.SnackbarHostState
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

@Composable
internal fun SettingsSecurityRoute(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    state: SecuritySettingsScreenState = rememberSecuritySettingsScreenState(),
) {
    val uiState = state.uiState

    SettingsSecurityScreen(
        uiState = uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onChangeAuthEnabled = onChangeAuthEnabled,
        onPasswordChangeClick = onPasswordChangeClick,
        onChangeBiometricEnabled = state::onChangeBiometricEnabled,
        onChangeBackgroundLockEnabled = state::onChangeBackgroundLockEnabled,
    )

    if (uiState.isBiometricsDialogShow) {
        BiometricsDialog(
            onConfirmClick = state::onBiometricsDialogClick,
            onDismissRequest = state::onBiometricsDialogDismissRequest
        )
    }

    LifecycleResumeEffect {
        state.onResume()
    }
}

@Stable
internal class ChildSecuritySettingsScreenState(
    private val context: Context,
    private val scope: CoroutineScope,
    private val viewModel: SettingsSecurityViewModel,
    override val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    private val biometricsDialogController: BiometricsDialogController = BiometricsDialogController(),
) : SecuritySettingsScreenState {

    override fun onBiometricsDialogClick() {
        throw NotImplementedError("")
    }

    private val biometricManager = BiometricManager.from(context)

    override fun activityResult(activityResult: ActivityResult) {
        when (biometricManager.canAuthenticateWeak()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                startBimetric()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                logcat { "認証 Not 有効化 リトライ" }
                scope.launch {
                    snackbarHostState.showSnackbar("生体認証が有効になっていません。")
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
                    snackbarHostState.showSnackbar("このデバイスは生体認証が利用できません。")
                }
            }
        }
    }

    private fun startBimetric() {
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
                        snackbarHostState.showSnackbar("生体認証を有効にしました。")
                    }
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("アプリ内の生体認証")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setSubtitle("アプリ内で生体認証を有効化するために、認証してください。")
            .setNegativeButtonText("Cancel")
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
                    startBimetric()
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // 生体認証が設定されていないため、設定を促す
                    uiState = uiState.copy(isBiometricsDialogShow = true)
                    biometricsDialogController.show(Unit)
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    // 生体認証が一時的に利用不可のため、エラーメッセージ表示
                    scope.launch {
                        snackbarHostState.showSnackbar("生体認証が一時的に利用できません。あとでもう一度試してみてください。")
                    }
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
                -> {
                    // 生体認証が利用不可のため、エラーメッセージ表示
                    scope.launch {
                        snackbarHostState.showSnackbar("このデバイスは生体認証が利用できません。")
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
                            snackbarHostState.showSnackbar("生体認証を無効にしました。")
                        }
                    }
                }
            )
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle("アプリ内の生体認証")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setSubtitle("アプリ内で生体認証を無効化するために、認証してください。")
                .setNegativeButtonText("Cancel")
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
        biometricsDialogController.dismiss()
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

internal class BiometricsDialogController : DialogController<Unit>(Unit)

@Stable
private class RealSecuritySettingsScreenState(
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
    RealSecuritySettingsScreenState(state = state, resultLauncher = resultLauncher)
}

@Composable
internal fun rememberChildSecuritySettingsScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SettingsSecurityViewModel = hiltViewModel(),
): SecuritySettingsScreenState = remember {
    ChildSecuritySettingsScreenState(
        context = context,
        scope = scope,
        viewModel = viewModel
    )
}

internal data class SecuritySettingsScreenUiState(
    val isAuthEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val isBackgroundLockEnabled: Boolean = false,
    val isBiometricsDialogShow: Boolean = false,
)

@Composable
private fun SettingsSecurityScreen(
    uiState: SecuritySettingsScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onChangeBiometricEnabled: (Boolean) -> Unit,
    onChangeBackgroundLockEnabled: (Boolean) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_security_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHostState = snackbarHostState,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(contentPadding = contentPadding) {
            SwitchSetting(
                title = R.string.settings_security_title_password_lock,
                checked = uiState.isAuthEnabled,
                onCheckedChange = onChangeAuthEnabled,
                summary = R.string.settings_security_summary_password_lock,
            )
            Setting(
                title = R.string.settings_security_title_change_password,
                onClick = onPasswordChangeClick,
                enabled = uiState.isAuthEnabled
            )
            SwitchSetting(
                title = R.string.settings_security_title_use_biometric_auth,
                checked = uiState.isBiometricEnabled,
                onCheckedChange = onChangeBiometricEnabled,
                summary = R.string.settings_security_summary_use_biometric_auth,
                enabled = uiState.isAuthEnabled,
            )
            SwitchSetting(
                title = R.string.settings_security_label_background_lock,
                checked = uiState.isBackgroundLockEnabled,
                onCheckedChange = onChangeBackgroundLockEnabled,
                enabled = uiState.isAuthEnabled,
            )
        }
    }
}

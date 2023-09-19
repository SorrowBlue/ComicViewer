package com.sorrowblue.comicviewer.feature.settings.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.feature.settings.security.section.BiometricsRequestDialogUiState
import com.sorrowblue.comicviewer.feature.settings.security.section.PasswordDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class SettingsSecurityViewModel @Inject constructor(
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    @ApplicationContext context: Context,
) : ViewModel(), DefaultLifecycleObserver {

    private var biometricPromptMode = false
    private val biometricManager = BiometricManager.from(context)

    private val _uiState = MutableStateFlow(SettingsSecurityScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents =
        MutableSharedFlow<SettingsSecurityUiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        manageSecuritySettingsUseCase.settings.onEach {
            val uiState = _uiState.value
            val settingsSecuritySheetUiState = uiState.settingsSecuritySheetUiState.copy(
                isAuthEnabled = it.password != null,
                isBackgroundLockEnabled = it.lockOnBackground,
                isBiometricEnabled = it.useBiometrics
            )
            _uiState.value =
                uiState.copy(settingsSecuritySheetUiState = settingsSecuritySheetUiState)
        }.launchIn(viewModelScope)
    }

    val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            logcat { "Authentication error: $errString" }
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            logcat { "Authentication succeeded!" }
            if (biometricPromptMode) {
                logcat { "整体認証を有効にする" }
                viewModelScope.launch {
                    manageSecuritySettingsUseCase.edit {
                        it.copy(useBiometrics = true)
                    }
                    _uiEvents.emit(SettingsSecurityUiEvent.Message.Text("生体認証を有効にしました。"))
                }
            } else {
                logcat { "整体認証を無効にする" }
                viewModelScope.launch {
                    manageSecuritySettingsUseCase.edit {
                        it.copy(useBiometrics = false)
                    }
                    _uiEvents.emit(SettingsSecurityUiEvent.Message.Text("生体認証を無効にしました。"))
                }
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            logcat { "Authentication failed" }
        }
    }

    fun onResult() {
        when (biometricManager.canAuthenticateWeak()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPromptMode = true
                _uiEvents.tryEmit(SettingsSecurityUiEvent.BiometricPrompt(true))
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                logcat { "認証 Not 有効化 リトライ" }
                _uiEvents.tryEmit(SettingsSecurityUiEvent.Message.Text("生体認証が有効になっていません。"))
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                logcat { "生体認証 利用不可" }
                _uiEvents.tryEmit(SettingsSecurityUiEvent.Message.Text("このデバイスは生体認証が利用できません。"))
            }
        }
    }

    fun onChangeBackgroundLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            manageSecuritySettingsUseCase.edit {
                it.copy(lockOnBackground = enabled)
            }
        }
    }

    fun onChangeBiometricEnabled(enabled: Boolean) {
        if (enabled) {
            when (biometricManager.canAuthenticateWeak()) {
                BiometricManager.BIOMETRIC_SUCCESS,
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                    // 生体認証が有効なため、認証する
                    biometricPromptMode = true
                    _uiEvents.tryEmit(SettingsSecurityUiEvent.BiometricPrompt(true))
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    // 生体認証が設定されていないため、設定を促す
                    _uiState.value =
                        _uiState.value.copy(biometricsRequestDialogUiState = BiometricsRequestDialogUiState.Show)
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    // 生体認証が一時的に利用不可のため、エラーメッセージ表示
                    _uiEvents.tryEmit(SettingsSecurityUiEvent.Message.Text("生体認証が利用できません。あとでもう一度試してみてください。"))
                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                    // 生体認証が利用不可のため、エラーメッセージ表示
                    _uiEvents.tryEmit(SettingsSecurityUiEvent.Message.Text("このデバイスは生体認証が利用できません。"))
                }
            }
        } else {
            // 生体認証を無効化したいので認証する。
            biometricPromptMode = false
            _uiEvents.tryEmit(SettingsSecurityUiEvent.BiometricPrompt(false))
        }
    }

    fun onPasswordChange(text: String) {
        val uiState = _uiState.value
        val newUiState = when (val passwordDialogUiState = uiState.passwordDialogUiState) {
            PasswordDialogUiState.Hide -> return
            is PasswordDialogUiState.Activation -> passwordDialogUiState.copy(password = text)
            is PasswordDialogUiState.Change -> passwordDialogUiState.copy(newPassword = text)
            is PasswordDialogUiState.Invalidation -> passwordDialogUiState.copy(password = text)
        }
        _uiState.value = uiState.copy(passwordDialogUiState = newUiState)
    }

    fun onOldPasswordChange(text: String) {
        val uiState = _uiState.value
        val newUiState = when (val passwordDialogUiState = uiState.passwordDialogUiState) {
            PasswordDialogUiState.Hide -> return
            is PasswordDialogUiState.Activation -> return
            is PasswordDialogUiState.Change -> passwordDialogUiState.copy(oldPassword = text)
            is PasswordDialogUiState.Invalidation -> return
        }
        _uiState.value = uiState.copy(passwordDialogUiState = newUiState)
    }

    fun onPasswordDialogDismissRequest() {
        _uiState.value = _uiState.value.copy(passwordDialogUiState = PasswordDialogUiState.Hide)
    }

    fun onPasswordDialogConfirmClick() {
        val uiState = _uiState.value
        when (val passwordDialogUiState = uiState.passwordDialogUiState) {
            PasswordDialogUiState.Hide -> return
            is PasswordDialogUiState.Activation -> {
                updateAuth(true, passwordDialogUiState.password)
                PasswordDialogUiState.Hide
            }

            is PasswordDialogUiState.Change -> {
                updateAuth(
                    true,
                    passwordDialogUiState.newPassword,
                    passwordDialogUiState.oldPassword
                )
            }

            is PasswordDialogUiState.Invalidation -> {
                updateAuth(false, passwordDialogUiState.password)
            }
        }
    }

    private fun updateAuth(enabled: Boolean, password: String, oldPassword: String? = null) {
        viewModelScope.launch {
            var hide = false
            if (enabled) {
                if (oldPassword == null) {
                    // 新規登録
                    hide = true
                    manageSecuritySettingsUseCase.edit {
                        it.copy(password = password)
                    }
                } else {
                    // 変更
                    manageSecuritySettingsUseCase.edit {
                        if (it.password == oldPassword) {
                            // パスワードが一致した場合、更新する
                            hide = true
                            it.copy(password = password)
                        } else {
                            // パスワードが一致しなかった場合、エラーメッセージを表示して変更しない。
                            val uiState = _uiState.value
                            _uiState.value = uiState.copy(
                                passwordDialogUiState = PasswordDialogUiState.Change(
                                    newPassword = password,
                                    oldPassword = oldPassword,
                                    error = R.string.settings_security_password_manage_dialog_error_passwords_do_not_match
                                )
                            )
                            it
                        }
                    }
                }
            } else {
                manageSecuritySettingsUseCase.edit {
                    if (it.password == password) {
                        // パスワードが一致した場合、無効にする
                        hide = true
                        it.copy(password = null, useBiometrics = false, lockOnBackground = false)
                    } else {
                        // パスワードが一致しなかった場合、エラーメッセージを表示して変更しない。
                        val uiState = _uiState.value
                        _uiState.value = uiState.copy(
                            passwordDialogUiState = PasswordDialogUiState.Invalidation(
                                password = password,
                                error = R.string.settings_security_password_manage_dialog_error_passwords_do_not_match
                            )
                        )
                        it
                    }
                }
            }
            if (hide) {
                _uiState.value =
                    _uiState.value.copy(passwordDialogUiState = PasswordDialogUiState.Hide)
            }
        }
    }

    fun onBiometricsRequestDialogDismissRequest() {
        _uiState.value =
            _uiState.value.copy(biometricsRequestDialogUiState = BiometricsRequestDialogUiState.Hide)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModelScope.launch {
            if (manageSecuritySettingsUseCase.settings.first().useBiometrics) {
                when (biometricManager.canAuthenticateWeak()) {
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        // 生体認証が設定されていないため、無効にする。
                        manageSecuritySettingsUseCase.edit {
                            it.copy(useBiometrics = false)
                        }
                    }

                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                    BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                        // 生体認証が利用不可
                        manageSecuritySettingsUseCase.edit {
                            it.copy(useBiometrics = false)
                        }
                    }

                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                    BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                        // 生体認証が一時的に利用不可
                        manageSecuritySettingsUseCase.edit {
                            it.copy(useBiometrics = false)
                        }
                    }

                    BiometricManager.BIOMETRIC_SUCCESS,
                    BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> Unit
                }
            }
        }
    }
}

package com.sorrowblue.comicviewer.settings.security.password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class ManagePasswordViewModel @Inject constructor(
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    fun updatePassword(oldPassword: String?, newValue: String?, done: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (oldPassword == null || settings.first().password == oldPassword) {
                manageSecuritySettingsUseCase.edit { it.copy(password = newValue) }
                done(true)
            } else {
                done(false)
            }
        }
    }

    private val args: ManagePasswordDialogArgs by navArgs()

    val settings = manageSecuritySettingsUseCase.settings
    val state: PasswordManageState = args.state

    val password = MutableStateFlow("")
}

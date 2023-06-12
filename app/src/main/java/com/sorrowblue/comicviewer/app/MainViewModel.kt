package com.sorrowblue.comicviewer.app

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel(), TextView.OnEditorActionListener {

    val settings = loadSettingsUseCase.settings
    val securitySettingsFlow = manageSecuritySettingsUseCase.settings
    fun getNavigationHistory(): Flow<NavigationHistory?> =
        getNavigationHistoryUseCase.execute(EmptyRequest).map { it.dataOrNull }

    val doneTutorialFlow = loadSettingsUseCase.settings.map { it.doneTutorial }

    val isShownAuthSheet = MutableStateFlow(runBlocking { securitySettingsFlow.first().password != null })
    val password = MutableStateFlow("")
    val authError = MutableStateFlow(0)

    fun go() {
        if (password.value == runBlocking { securitySettingsFlow.first() }.password) {
            isShownAuthSheet.value = false
        } else {
            authError.value = R.string.message_incorrent_password
        }
        password.value = ""
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        return if (actionId == EditorInfo.IME_ACTION_GO) {
            if (password.value == runBlocking { securitySettingsFlow.first() }.password) {
                isShownAuthSheet.value = false
            } else {
                authError.value = R.string.message_incorrent_password
            }
            password.value = ""
            true
        } else {
            false
        }
    }
}

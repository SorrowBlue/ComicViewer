package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    private val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    val settings = loadSettingsUseCase.settings
    val securitySettingsFlow = manageSecuritySettingsUseCase.settings

    fun getNavigationHistory(): Flow<NavigationHistory?> = getNavigationHistoryUseCase.execute(EmptyRequest).map { it.dataOrNull }
}

package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
internal actual fun rememberRootScreenWrapperState(): RootScreenWrapperState {
    return rememberRootScreenWrapperState(viewModel = hiltViewModel())
}

@HiltViewModel
internal actual class RootScreenWrapperViewModel @Inject constructor(
    actual val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    actual val loadSettingsUseCase: LoadSettingsUseCase,
) : ViewModel()

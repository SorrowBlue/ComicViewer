package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import org.koin.android.annotation.KoinViewModel


@Composable
internal actual fun rememberRootScreenWrapperState(): RootScreenWrapperState {
    return rememberRootScreenWrapperState(viewModel = koinViewModel())
}

@KoinViewModel
internal actual class RootScreenWrapperViewModel(
    actual val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    actual val loadSettingsUseCase: LoadSettingsUseCase,
) : ViewModel()

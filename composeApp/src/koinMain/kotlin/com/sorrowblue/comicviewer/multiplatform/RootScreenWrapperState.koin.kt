package com.sorrowblue.comicviewer.multiplatform

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.RootScreenWrapperState
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
internal actual fun rememberRootScreenWrapperState(): RootScreenWrapperState {
    return com.sorrowblue.comicviewer.rememberRootScreenWrapperState(viewModel = com.sorrowblue.comicviewer.RootScreenWrapperViewModel(
        object : ManageSecuritySettingsUseCase {
            override val settings = MutableStateFlow(SecuritySettings())

            override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
                settings.value = action(settings.value)
            }
        },
        object : LoadSettingsUseCase {
            override val settings = MutableStateFlow(Settings())

            override suspend fun edit(action: (Settings) -> Settings) {
                settings.value = action(settings.value)
            }
        }
    ))
}

internal actual class RootScreenWrapperViewModel(
    actual val manageSecuritySettingsUseCase: ManageSecuritySettingsUseCase,
    actual val loadSettingsUseCase: LoadSettingsUseCase,
) : ViewModel()

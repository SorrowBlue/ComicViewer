package com.sorrowblue.comicviewer.domain.usecase.settings

import kotlinx.coroutines.flow.Flow

abstract class ManageSettingsUseCase<T> {

    abstract val settings: Flow<T>

    abstract suspend fun edit(action: (T) -> T)
}

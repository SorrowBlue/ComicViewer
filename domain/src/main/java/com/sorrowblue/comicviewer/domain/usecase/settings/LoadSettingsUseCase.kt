package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
import com.sorrowblue.comicviewer.domain.model.settings.Settings

abstract class LoadSettingsUseCase : ManageSettingsUseCase<Settings>()

abstract class ManageOneTimeFlagUseCase : ManageSettingsUseCase<OneTimeFlag>()

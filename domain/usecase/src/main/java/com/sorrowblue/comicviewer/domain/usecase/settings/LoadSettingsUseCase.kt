package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
import com.sorrowblue.comicviewer.domain.model.settings.Settings

interface LoadSettingsUseCase : ManageSettingsUseCase<Settings>

interface ManageOneTimeFlagUseCase : ManageSettingsUseCase<OneTimeFlag>

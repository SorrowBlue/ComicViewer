/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.domain.usecase.settings.ManageWindowSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

actual interface AppGraph :
    ViewModelGraph,
    NavigationGraph {
    actual val context: PlatformContext
    val settingsUseCase: ManageWindowSettingsUseCase
}

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import org.koin.ksp.generated.module

internal actual fun allModules() = listOf(
    DiModule().module,
    SettingsModule().module,
    AppModule().module
)

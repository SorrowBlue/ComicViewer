package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import org.koin.ksp.generated.module

internal actual fun allModules() = listOf(
    DiModule().module,
    AppModule().module
)

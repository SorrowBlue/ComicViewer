package com.sorrowblue.comicviewer.app

import org.koin.core.module.Module
import org.koin.dsl.KoinConfiguration

internal expect fun koinConfiguration(): KoinConfiguration

internal expect fun allModules(): List<Module>

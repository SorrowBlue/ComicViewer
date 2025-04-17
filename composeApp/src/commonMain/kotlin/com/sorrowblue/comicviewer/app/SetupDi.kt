package com.sorrowblue.comicviewer.app

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.KoinConfiguration

internal expect fun setupDi(): KoinConfiguration

@Module
@ComponentScan("com.sorrowblue.comicviewer")
class AppModule

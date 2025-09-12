package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@Module(includes = [DiModule::class])
@Configuration
@ComponentScan("com.sorrowblue.comicviewer.app")
internal class AppModule

@KoinApplication
internal object MainApp

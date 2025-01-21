package com.sorrowblue.comicviewer.app

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.sorrowblue.comicviewer.data.di.DiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.Module
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

/**
 * Main application
 */
@OptIn(KoinExperimentalAPI::class)
internal class MainApplication : SplitCompatApplication(), KoinStartup, KoinComponent {

    override fun onKoinStartup() = KoinConfiguration {
        androidLogger()
        androidContext(this@MainApplication)
        modules(appModule)
        modules(defaultModule)
        modules(DiModule().module)
        modules(AppModule().module)
        workManagerFactory()
    }
}

annotation class IoDispatcher

annotation class DefaultDispatcher

@Module
@ComponentScan("com.sorrowblue.comicviewer")
class AppModule

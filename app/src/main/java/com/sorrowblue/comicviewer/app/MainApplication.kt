package com.sorrowblue.comicviewer.app

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.sorrowblue.comicviewer.data.di.DiModule
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

/**
 * Main application
 */
@OptIn(KoinExperimentalAPI::class)
@HiltAndroidApp
internal class MainApplication : SplitCompatApplication(), Configuration.Provider, KoinStartup {

    override fun onKoinStartup() = KoinConfiguration {
        androidLogger()
        androidContext(this@MainApplication)
        modules(appModule)
        modules(defaultModule)
        modules(DiModule().module)
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}

annotation class IoDispatcher

annotation class DefaultDispatcher

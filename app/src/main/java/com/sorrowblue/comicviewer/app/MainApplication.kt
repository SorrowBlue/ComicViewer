package com.sorrowblue.comicviewer.app

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.play.core.splitcompat.SplitCompatApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

@HiltAndroidApp
internal class MainApplication : SplitCompatApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        startKoin {
            loadKoinModules(appModule)
            androidContext(this@MainApplication)
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()
}

private val appModule = module {
    single(named<IoDispatcher>()) { Dispatchers.IO }
    single(named<DefaultDispatcher>()) { Dispatchers.Default }
}

annotation class IoDispatcher

annotation class DefaultDispatcher

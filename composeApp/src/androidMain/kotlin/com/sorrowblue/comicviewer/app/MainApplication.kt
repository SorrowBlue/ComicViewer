package com.sorrowblue.comicviewer.app

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.play.core.splitcompat.SplitCompatApplication
import dagger.hilt.android.HiltAndroidApp


internal class MainApplication : SplitCompatApplication(), Configuration.Provider {

    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}

annotation class IoDispatcher

annotation class DefaultDispatcher

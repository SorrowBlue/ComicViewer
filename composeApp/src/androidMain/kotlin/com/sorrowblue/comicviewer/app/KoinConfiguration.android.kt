package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.aggregation.di.AggregationModule
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

internal actual fun koinConfiguration() = KoinConfiguration {
    logcat(LogPriority.INFO) { "onKoinStartup" }
    androidLogger()
    workManagerFactory()
}

internal actual fun allModules() =
    listOf(
        AggregationModule().module,
        AppModule().module,
    )

package com.sorrowblue.comicviewer.data.datastore.startup

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.data.datastore.di.DataStoreGraph
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import com.sorrowblue.comicviewer.framework.common.platformGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

internal class DarkModeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        require(context.platformGraph is DataStoreGraph.Factory)
        val darkMode =
            with((context.platformGraph as DataStoreGraph.Factory).createDataStoreGraph()) {
                runBlocking { datastoreDataSource.displaySettings.first() }.darkMode
            }
        when (darkMode) {
            DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        }.let(AppCompatDelegate::setDefaultNightMode)
        logcat(LogPriority.INFO) { "Initialized nightMode. $darkMode." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)
}

package com.sorrowblue.comicviewer.data.datastore.startup

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("unused")
internal class DarkModeInitializer : Initializer<Unit>, KoinComponent {

    private val datastoreDataSource: DatastoreDataSource by inject()

    override fun create(context: Context) {
        val darkMode = runBlocking { datastoreDataSource.displaySettings.first() }.darkMode
        when (darkMode) {
            DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        }.let(AppCompatDelegate::setDefaultNightMode)
        logcat(LogPriority.INFO) { "Initialized nightMode. $darkMode." }
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies() = listOf(LogcatInitializer::class.java, KoinInitializer::class.java)
}

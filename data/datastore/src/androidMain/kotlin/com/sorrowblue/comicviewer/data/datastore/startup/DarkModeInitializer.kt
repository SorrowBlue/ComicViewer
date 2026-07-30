/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.startup

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import com.sorrowblue.comicviewer.framework.common.appGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.HasMemberInjections
import dev.zacsweers.metro.Inject
import io.github.vinceglb.filekit.initializer.FileKitInitializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@HasMemberInjections
class DarkModeInitializer : Initializer<Unit> {

    @Inject
    lateinit var datastoreDataSource: DatastoreDataSource

    override fun create(context: Context) {
        context.appGraph<DarkModeInitializerInjector>().inject(this)
        val darkMode = runBlocking { datastoreDataSource.displaySettings.first() }.darkMode
        when (darkMode) {
            DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        }.let(AppCompatDelegate::setDefaultNightMode)
        logcat(LogPriority.INFO) { "Initialized nightMode. $darkMode." }
    }

    override fun dependencies() = listOf(
        LogcatInitializer::class.java,
        FileKitInitializer::class.java,
    )
}

@ContributesTo(AppScope::class)
interface DarkModeInitializerInjector {
    fun inject(target: DarkModeInitializer)
}

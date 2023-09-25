package com.sorrowblue.comicviewer.feature.settings.display

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.logcat

@Suppress("unused")
internal class DarkModeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val useCase = EntryPointAccessors.fromApplication<InitializerInterface>(context)
            .displaySettingsUseCase()
        val darkMode = runBlocking { useCase.settings.first() }.darkMode
        when (darkMode) {
            DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        }.let(AppCompatDelegate::setDefaultNightMode)
        logcat(LogPriority.INFO) { "Initialized NightMode is $darkMode." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface InitializerInterface {
        fun displaySettingsUseCase(): ManageDisplaySettingsUseCase
    }
}

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.koin.compose.KoinContext
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

fun MainViewController() = ComposeUIViewController {

    KoinMultiplatformApplication(config = KoinConfiguration {
        LogcatLogger.install(PrintLogger)
        modules(DiModule().module)
        modules(SettingsModule().module)
        defaultModule()
    }) {
//        val initializer: CoilInitializer = koinInject()
//        val sevenZipInitializer: SevenZipInitializer = koinInject()
//        LaunchedEffect(Unit) {
//            initializer.create()
//            sevenZipInitializer.create()
//        }
        ComicTheme {
            KoinContext {
                RootScreenWrapper(finishApp = {}) {
                    ComicViewerApp(rememberComicViewerAppState())
                }
            }
        }
    }
}
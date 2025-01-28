package com.sorrowblue.comicviewer

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sorrowblue.comicviewer.app.ComicViewerApp
import com.sorrowblue.comicviewer.app.RootScreenWrapper
import com.sorrowblue.comicviewer.data.coil.CoilInitializer
import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.data.reader.zip.impl.SevenZipInitializer
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import java.awt.Dimension
import logcat.LogcatLogger
import logcat.PrintLogger
import org.koin.compose.KoinApplication
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.defineAndroidSplitInstallManager
import org.koin.ksp.generated.defineTutorialNavGraphNavigatorImpl
import org.koin.ksp.generated.module

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicViewer",
    ) {
        window.minimumSize = Dimension(400, 600)
        KoinApplication(application = {
            LogcatLogger.install(PrintLogger)
            modules(DiModule().module)
            modules(SettingsModule().module)
            defaultModule()
            module {
                defineTutorialNavGraphNavigatorImpl()
                defineAndroidSplitInstallManager()
            }
        }) {
            val initializer: CoilInitializer = koinInject()
            val sevenZipInitializer: SevenZipInitializer = koinInject()
            LaunchedEffect(Unit) {
                initializer.create()
                sevenZipInitializer.create()
            }
            ComicTheme {
                KoinContext {
                    RootScreenWrapper(finishApp = ::exitApplication) {
                        ComicViewerApp()
                    }
                }
            }
        }
    }
}

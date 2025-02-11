package com.sorrowblue.comicviewer

import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.findComposeDefaultViewModelStoreOwner
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import com.sorrowblue.comicviewer.app.ComicViewerApp
import com.sorrowblue.comicviewer.app.RootScreenWrapper
import com.sorrowblue.comicviewer.data.coil.CoilInitializer
import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.data.reader.zip.impl.SevenZipInitializer
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.composeapp.generated.resources.Res
import comicviewer.composeapp.generated.resources.app_label_exit
import java.awt.Dimension
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.koinInject
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.defineAndroidSplitInstallManager
import org.koin.ksp.generated.module

@OptIn(InternalComposeApi::class)
fun ma2in() = application {
    val trayState = rememberTrayState()
    val notification = rememberNotification("Notification", "Message from MyApp!")
    Tray(
        state = trayState,
        icon = androidx.compose.ui.graphics.vector.rememberVectorPainter(ComicIcons.Launcher),
        onAction = {
            trayState.sendNotification(notification)
        },
        menu = {
            Item(stringResource(Res.string.app_label_exit), onClick = ::exitApplication)
        },
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "ComicViewer",
    ) {
        val owner = findComposeDefaultViewModelStoreOwner()
        LaunchedEffect(Unit) {
            logcat { "findComposeDefaultViewModelStoreOwner=$owner" }
        }
        trayState.sendNotification(notification)
        window.minimumSize = Dimension(400, 600)
        KoinMultiplatformApplication(config = KoinConfiguration {
            LogcatLogger.install(PrintLogger)
            modules(DiModule().module)
            modules(SettingsModule().module)
            defaultModule()
            module {
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

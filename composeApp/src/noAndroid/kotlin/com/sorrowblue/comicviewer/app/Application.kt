package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import org.koin.compose.KoinContext
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.currentKoinScope
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@Composable
fun Application(finishApp: () -> Unit) {
    KoinMultiplatformApplication(config = setupDi()) {
        val initializing = koinInjectAll<Initializer<*>>()
        LaunchedEffect(Unit) {
            Initializer.initialize(initializing)
        }
        ComicTheme {
            KoinContext {
                RootScreenWrapper(finishApp = finishApp) {
                    ComicViewerApp()
                }
            }
        }
    }
}

@Composable
private inline fun <reified T> koinInjectAll(
    qualifier: Qualifier? = null,
    scope: Scope = currentKoinScope(),
): List<T> {
    return remember(qualifier, scope) {
        scope.getAll(T::class)
    }
}

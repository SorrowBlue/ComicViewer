package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.framework.common.AppCoroutineContext
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import org.koin.compose.KoinApplication
import org.koin.compose.currentKoinScope
import org.koin.compose.module.rememberKoinModules
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import org.koin.dsl.module

@Composable
fun Application(finishApp: () -> Unit) {
    @Suppress("OPT_IN_USAGE")
    (
        KoinApplication(
            application = koinConfiguration().invoke()
        ) {
            val coroutineScope = rememberCoroutineScope()
            @Suppress("OPT_IN_USAGE")
            rememberKoinModules {
                listOf(
                    module {
                        single(TypeQualifier(AppCoroutineContext::class)) { coroutineScope }
                    }
                )
            }
            val initializing = koinInjectAll<Initializer<*>>()
            LaunchedEffect(Unit) {
                Initializer.initialize(initializing)
            }
            ComicTheme {
                RootScreenWrapper(finishApp = finishApp) {
                    ComicViewerApp()
                }
            }
        }
        )
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

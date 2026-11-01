/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.ViewModelProvider
import com.sorrowblue.comicviewer.Application
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import comicviewer.framework.ui.generated.resources.app_name
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import org.jetbrains.compose.resources.stringResource

/**
 * Main window for the JVM Desktop application.
 */
@ContributesIntoMap(AppScope::class, binding<JvmWindow>())
@WindowKey
class MainWindow(viewModelFactory: ViewModelProvider.Factory) : JvmWindow(viewModelFactory) {

    private val viewModel by viewModels<ComicViewerAppViewModel>()

    override val title: String
        @Composable get() = stringResource(FrameworkResString.app_name)

    override val icon: Painter
        @Composable get() = rememberWindowIconPainter(ComicIcons.Launcher)

    @Composable
    context(scope: FrameWindowScope, context: PlatformContext)
    override fun WindowContent() {
        Application(finishApp = ::close)
        SplashScreen(keepOnScreenCondition = viewModel.shouldKeepSplash::value)
    }
}

@Composable
private fun rememberWindowIconPainter(image: ImageVector): VectorPainter = rememberVectorPainter(
    defaultWidth = image.defaultWidth,
    defaultHeight = image.defaultHeight,
    viewportWidth = image.viewportWidth,
    viewportHeight = image.viewportHeight,
    name = image.name,
    tintColor = image.tintColor,
    tintBlendMode = image.tintBlendMode,
    autoMirror = image.autoMirror,
) { _, _ ->
    RenderVectorGroup(group = image.root)
}

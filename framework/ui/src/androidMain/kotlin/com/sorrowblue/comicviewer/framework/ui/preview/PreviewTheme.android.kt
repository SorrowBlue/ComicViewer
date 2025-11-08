package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.preview.fake.PreviewImage

@OptIn(ExperimentalCoilApi::class)
@Composable
actual fun PreviewTheme(content: @Composable () -> Unit) {
    SharedTransitionLayout {
        AnimatedContent(true) {
            if (it) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedContentScope provides this,
                ) {
                    ComicTheme {
                        val context = LocalContext.current
                        val previewHandler = AsyncImagePreviewHandler { PreviewImage(context) }
                        CompositionLocalProvider(
                            LocalAsyncImagePreviewHandler provides previewHandler,
                            content,
                        )
                    }
                }
            }
        }
    }
}

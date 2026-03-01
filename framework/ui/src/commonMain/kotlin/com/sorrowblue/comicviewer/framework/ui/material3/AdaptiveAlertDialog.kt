package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BasicAlertDialogOverride
import androidx.compose.material3.BasicAlertDialogOverrideScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.LocalBasicAlertDialogOverride
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass

internal val DialogMinWidth = 280.dp
internal val DialogMaxWidth = 560.dp

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3ComponentOverrideApi
object FixedDefaultBasicAlertDialogOverride : BasicAlertDialogOverride {
    @Composable
    override fun BasicAlertDialogOverrideScope.BasicAlertDialog() {
        Dialog(onDismissRequest = onDismissRequest, properties = properties) {
            val isFullScreenDialog = isCompactWindowClass()
            Box(
                modifier =
                    modifier.then(
                        if (isFullScreenDialog) {
                            Modifier
                        } else {
                            Modifier.sizeIn(
                                minWidth = DialogMinWidth,
                                maxWidth = DialogMaxWidth
                            ).sizeIn(maxHeight = DialogMaxWidth)
                        },
                    ),
                propagateMinConstraints = true,
            ) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ComponentOverrideApi::class)
@Composable
fun AdaptiveAlertDialog(
    title: @Composable () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFullScreenDialog: Boolean = isCompactWindowClass(),
    content: @Composable ((PaddingValues) -> Unit),
) {
    val movableContent = remember {
        movableContentOf { contentPadding: PaddingValues -> content(contentPadding) }
    }
    CompositionLocalProvider(
        LocalBasicAlertDialogOverride provides FixedDefaultBasicAlertDialogOverride,
    ) {
        BasicAlertDialog(
            onDismissRequest = onBackClick,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier,
        ) {
            if (isFullScreenDialog) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = title,
                            navigationIcon = {
                                BackIconButton(onClick = onBackClick)
                            },
                            windowInsets = WindowInsets.safeDrawing.only(AppBarWindowInsets),
                        )
                    },
                    contentWindowInsets = WindowInsets.safeDrawing.add(ContentWindowInsets),
                    content = movableContent,
                )
            } else {
                AlertDialogContent(
                    title = title,
                    content = {
                        movableContent(it)
                    },
                )
            }
        }
    }
}

private val ContentWindowInsets
    @Composable
    get() = ComicTheme.dimension.run {
        WindowInsets(left = margin, right = margin, bottom = margin)
    }

private val AppBarWindowInsets get() = WindowInsetsSides.Horizontal + WindowInsetsSides.Top

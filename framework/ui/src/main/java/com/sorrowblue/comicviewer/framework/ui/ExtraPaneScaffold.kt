package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ExtraPaneScaffold(
    topBar: @Composable () -> Unit,
    contentPadding: PaddingValues,
    scaffoldDirective: PaneScaffoldDirective,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = topBar,
        modifier = if (scaffoldDirective.maxHorizontalPartitions == 1) {
            modifier
        } else {
            val dimension = ComicTheme.dimension
            Modifier
                .padding(contentPadding)
                .padding(dimension.margin)
                .clip(ComicTheme.shapes.large)
                .then(modifier)
        },
        contentWindowInsets = if (scaffoldDirective.maxHorizontalPartitions == 1) {
            contentPadding.asWindowInsets()
        } else {
            WindowInsets(0)
        },
        containerColor = if (scaffoldDirective.maxHorizontalPartitions == 1) {
            ComicTheme.colorScheme.surface
        } else {
            ComicTheme.colorScheme.surfaceContainer
        }
    ) {
        val screenState = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .drawVerticalScrollbar(screenState)
                .verticalScroll(screenState)
                .padding(bottom = 16.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
object ExtraPaneScaffoldDefault {

    @Composable
    fun TopAppBar(
        title: @Composable () -> Unit,
        onCloseClick: () -> Unit,
        scaffoldDirective: PaneScaffoldDirective,
        modifier: Modifier = Modifier,
    ) {
        androidx.compose.material3.TopAppBar(
            title = title,
            actions = {
                IconButton(onClick = onCloseClick) {
                    Icon(imageVector = ComicIcons.Close, contentDescription = "Close")
                }
            },
            windowInsets = if (scaffoldDirective.maxHorizontalPartitions == 1) {
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            } else {
                WindowInsets(0)
            },
            colors = if (scaffoldDirective.maxHorizontalPartitions == 1) {
                TopAppBarDefaults.topAppBarColors()
            } else {
                TopAppBarDefaults.topAppBarColors(containerColor = ComicTheme.colorScheme.surfaceContainer)
            },
            modifier = modifier
        )
    }
}

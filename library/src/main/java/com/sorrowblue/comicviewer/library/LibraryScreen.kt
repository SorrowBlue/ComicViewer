package com.sorrowblue.comicviewer.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.library.component.LibraryTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryRoute(
    contentPadding: PaddingValues,
    onFeatureClick: (LocalFeature) -> Unit,
) {
    LibraryScreen(contentPadding = contentPadding, onFeatureClick = onFeatureClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {},
    onFeatureClick: (LocalFeature) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
) {
    val localLayoutDirection = LocalLayoutDirection.current
    Scaffold(
        topBar = {
            LibraryTopAppBar(
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(
            left = contentPadding.calculateLeftPadding(localLayoutDirection),
            top = contentPadding.calculateTopPadding(),
            right = contentPadding.calculateRightPadding(localLayoutDirection),
            bottom = contentPadding.calculateBottomPadding()
        ),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            LocalFeature.entries.forEach {
                ListItem(
                    headlineContent = { Text(text = it.label) },
                    leadingContent = { Icon(imageVector = it.icon, contentDescription = null) },
                    modifier = Modifier.clickable(onClick = {
                        onFeatureClick(it)
                    })
                )
            }
            CloudStorage.entries.forEach {
                ListItem(
                    headlineContent = { Text(text = stringResource(id = it.titleRes)) },
                    leadingContent = {
                        Image(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.TwoTone.ArrowRight,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewLibraryScreen() {
    AppMaterialTheme {
        LibraryScreen()
    }
}

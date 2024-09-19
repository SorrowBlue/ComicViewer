package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsGraph
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsGraphTransitions
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Destination<AppInfoSettingsGraph>(
    style = AppInfoSettingsGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun LicenseScreen(navigator: SettingsExtraNavigator) {
    LicenseScreen(
        onBackClick = navigator::navigateUp,
    )
}

@Composable
internal fun LicenseScreen(onBackClick: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "License")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
}

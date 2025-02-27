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
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object License

@Destination<License>
@Composable
internal fun LicenseScreen(navigator: SettingsExtraNavigator = koinInject()) {
    LicenseScreen(onBackClick = navigator::navigateUp)
}

@Composable
private fun LicenseScreen(onBackClick: () -> Unit) {
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

@Composable
internal expect fun LibrariesContainer(modifier: Modifier = Modifier)

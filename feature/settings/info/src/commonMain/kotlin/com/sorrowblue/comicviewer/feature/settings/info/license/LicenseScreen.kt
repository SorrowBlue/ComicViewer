package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.libraryColors
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import comicviewer.feature.settings.info.generated.resources.Res
import comicviewer.feature.settings.info.generated.resources.settings_info_title_license
import org.jetbrains.compose.resources.stringResource

internal data class LicenseScreenUiState(val libs: Libs? = null, val openDialog: Library? = null)

@Composable
internal fun LicenseScreen(
    uiState: LicenseScreenUiState,
    onBackClick: () -> Unit,
    onLibraryClick: (Library) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings_info_title_license))
                },
                navigationIcon = {
                    BackIconButton(onClick = onBackClick)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val lazyListState = rememberLazyListState()
        LibrariesContainer(
            libraries = uiState.libs,
            lazyListState = lazyListState,
            contentPadding = contentPadding,
            onLibraryClick = onLibraryClick,
            colors = LibraryDefaults.libraryColors(
                libraryBackgroundColor = ComicTheme.colorScheme.surfaceContainer,
                libraryContentColor = ComicTheme.colorScheme.onSurface,
            ),
        )
    }
}

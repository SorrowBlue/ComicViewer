package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Developer
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.Organization
import com.mikepenz.aboutlibraries.entity.Scm
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.libraryColors
import com.sorrowblue.comicviewer.feature.settings.info.navigation.LicenseNavKey
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.info.generated.resources.Res
import comicviewer.feature.settings.info.generated.resources.settings_info_title_license
import org.jetbrains.compose.resources.stringResource

@NavDestination(LicenseNavKey::class)
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
                libraryBackgroundColor = ComicTheme.colorScheme.surface,
                libraryContentColor = ComicTheme.colorScheme.onSurface,
            ),
        )
    }
}

@NavPreview(LicenseNavKey::class)
@Preview
@Composable
private fun LicenseScreenPreview() = PreviewTheme {
    LicenseScreen(
        uiState = LicenseScreenUiState(
            libs = Libs(
                libraries = List(10) {
                    Library(
                        uniqueId = "$it",
                        artifactVersion = "1.0.$it",
                        name = "Library $it",
                        description = "Description $it",
                        website = "https://example.com/$it",
                        developers = listOf(
                            Developer(
                                name = "Developer $it",
                                organisationUrl = "https://example.com/developer/$it",
                            ),
                        ),
                        organization = Organization(
                            name = "Organization $it",
                            url = "https://example.com/organization/$it",
                        ),
                        scm = Scm(
                            connection = "scm:git:git://example.com/repo.git",
                            developerConnection = "scm:git:ssh://example.com/repo.git",
                            url = "https://example.com/scm/$it",
                        ),
                    )
                },
                licenses = emptySet(),
            ),
        ),
        onBackClick = {},
        onLibraryClick = {},
    )
}

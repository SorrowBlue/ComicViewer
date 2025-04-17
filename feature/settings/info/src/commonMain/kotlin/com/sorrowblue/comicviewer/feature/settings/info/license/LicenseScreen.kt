package com.sorrowblue.comicviewer.feature.settings.info.license

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarBox
import comicviewer.feature.settings.info.generated.resources.Res
import comicviewer.feature.settings.info.generated.resources.settings_info_title_license
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
internal data object License

@Destination<License>
@Composable
internal fun LicenseScreen(
    navigator: SettingsExtraNavigator = koinInject(),
    licenseeHelper: LicenseeHelper = koinInject(),
) {
    val libs by rememberLibraries {
        licenseeHelper.loadAboutlibraries().decodeToString()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings_info_title_license))
                },
                navigationIcon = {
                    BackIconButton(onClick = navigator::navigateUp)
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val lazyListState = rememberLazyListState()
        ScrollbarBox(
            state = lazyListState,
            padding = contentPadding.only(PaddingValuesSides.Vertical + PaddingValuesSides.End)
                .plus(PaddingValues(end = 8.dp)),
        ) {
            LibrariesContainerFixed(
                libraries = libs,
                lazyListState = lazyListState,
                contentPadding = contentPadding
            )
        }
    }
}

@Composable
fun LibrariesContainerFixed(
    libraries: Libs?,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val uriHandler = LocalUriHandler.current

    val libs = libraries?.libraries ?: persistentListOf()
    var openDialog by remember { mutableStateOf<Library?>(null) }

    Libraries(
        libraries = libs,
        modifier = modifier,
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        onLibraryClick = { library ->
            val license = library.licenses.firstOrNull()
            if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                openDialog = library
            } else if (!license?.url.isNullOrBlank()) {
                license?.url?.also {
                    try {
                        uriHandler.openUri(it)
                    } catch (t: Throwable) {
                        logcat(
                            tag = "LibrariesContainerFixed",
                            priority = LogPriority.ERROR
                        ) { t.asLog() }
                    }
                }
            }
        },
    )

    openDialog?.let {
        LicenseDialog(openDialog!!) {
            openDialog = null
        }
    }
}

@Composable
fun Libraries(
    libraries: ImmutableList<Library>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onLibraryClick: ((Library) -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        libraryItems(
            libraries = libraries,
        ) { library ->
            val license = library.licenses.firstOrNull()
            if (onLibraryClick != null) {
                onLibraryClick.invoke(library)
            } else if (!license?.url.isNullOrBlank()) {
                license?.url?.also {
                    try {
                        uriHandler.openUri(it)
                    } catch (t: Throwable) {
                        logcat { t.asLog() }
                    }
                }
            }
        }
    }
}

internal inline fun LazyListScope.libraryItems(
    libraries: ImmutableList<Library>,
    crossinline onLibraryClick: ((Library) -> Unit),
) {
    items(libraries) { library ->
        Library(library = library) {
            onLibraryClick.invoke(library)
        }
    }
}

@Composable
private fun Library(library: Library, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = {
            Text(
                text = library.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        overlineContent = if (library.author.isNotBlank()) {
            {
                Text(
                    text = library.author,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            null
        },
        supportingContent = {
            Column {
                Text(
                    text = library.description.orEmpty(),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                if (library.licenses.isNotEmpty()) {
                    FlowRow {
                        library.licenses.forEach {
                            Badge(containerColor = ComicTheme.colorScheme.primaryContainer) {
                                Text(text = it.name)
                            }
                        }
                    }
                }
            }
        },
        trailingContent = library.artifactVersion?.let { version ->
            {
                Text(
                    text = version,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    )
}

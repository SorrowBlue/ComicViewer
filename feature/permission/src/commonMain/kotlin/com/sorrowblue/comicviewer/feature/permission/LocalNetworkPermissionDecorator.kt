/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.get
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.permission.nav.LocalNetworkPermissionKey
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator

/**
 * Creates and remembers a [NavEntryDecorator] that intercepts navigation entries requiring
 * local network access permission (specified via [LocalNetworkPermissionKey]),
 * covering them with the permission request UI until permission is granted.
 *
 * @param isSmbBookshelf Function to determine whether the given [BookshelfId] corresponds to an SMB bookshelf.
 * Returns null if the bookshelf information is still loading.
 * @return A [NavEntryDecorator] protecting SMB routes.
 */
@Composable
fun rememberLocalNetworkPermissionDecorator(
    isSmbBookshelf: @Composable (BookshelfId) -> Boolean?,
): NavEntryDecorator<NavKey> {
    val permissionRequester = rememberLocalNetworkPermissionRequester(initCheck = true)
    val currentIsSmbBookshelf by rememberUpdatedState(isSmbBookshelf)

    return remember(permissionRequester) {
        NavEntryDecorator(
            decorate = { entry ->
                val metadata = entry.metadata[LocalNetworkPermissionKey]
                if (metadata == null) {
                    entry.Content()
                } else {
                    val bookshelfId = metadata.bookshelfId
                    val isSmb = if (bookshelfId != null) {
                        currentIsSmbBookshelf(bookshelfId)
                    } else {
                        true
                    }
                    when (isSmb) {
                        true -> {
                            LocalNetworkPermissionGuard(
                                permissionRequester = permissionRequester,
                                content = { entry.Content() },
                            )
                        }

                        false -> {
                            entry.Content()
                        }

                        null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ComicTheme.colorScheme.background),
                            )
                        }
                    }
                }
            },
            onPop = {},
        )
    }
}

@Composable
private fun LocalNetworkPermissionGuard(
    permissionRequester: LocalNetworkPermissionRequester,
    content: @Composable () -> Unit,
) {
    if (permissionRequester.state !is LocalNetworkPermissionState.Granted) {
        val navigator = LocalNavigator.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ComicTheme.colorScheme.background),
        ) {
            LocalNetworkAccessPermissionDialog(
                isRationale = permissionRequester.state is LocalNetworkPermissionState.Rationale,
                onConfirmClick = permissionRequester::onPermissionConfirmClick,
                onDismissClick = { navigator.goBack() },
            )
        }
    } else {
        content()
    }
}

/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.get
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.common.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfUseCase
import com.sorrowblue.comicviewer.feature.permission.nav.LocalNetworkPermissionKey
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesIntoSet(AppScope::class)
internal class LocalNetworkPermissionDecorator : NavEntryDecoratorProvider {

    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> =
        rememberLocalNetworkPermissionDecorator()
}

/**
 * Creates and remembers a [NavEntryDecorator] that intercepts navigation entries requiring
 * local network access permission (specified via [LocalNetworkPermissionKey]),
 * covering them with the permission request UI until permission is granted.
 *
 * @return A [NavEntryDecorator] protecting SMB routes.
 */
@Composable
private fun rememberLocalNetworkPermissionDecorator(
    viewModel: LocalNetworkPermissionViewModel = metroViewModel<LocalNetworkPermissionViewModel>(),
): NavEntryDecorator<NavKey> {
    val permissionRequester = rememberLocalNetworkPermissionRequester(initCheck = true)
    return remember(permissionRequester, viewModel) {
        NavEntryDecorator(
            decorate = { entry ->
                val metadata = entry.metadata[LocalNetworkPermissionKey]
                if (metadata == null) {
                    entry.Content()
                } else {
                    val bookshelfId = metadata.bookshelfId
                    val isSmb = if (bookshelfId != null) {
                        viewModel.isSmbBookshelf(bookshelfId)
                            .collectAsStateWithLifecycle(null).value
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

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class LocalNetworkPermissionViewModel(
    private val flowBookshelfUseCase: FlowBookshelfUseCase,
) : ViewModel() {

    fun isSmbBookshelf(bookshelfId: BookshelfId): Flow<Boolean> =
        flowBookshelfUseCase(bookshelfId).map { resource ->
            resource.dataOrNull()?.type == BookshelfType.SMB
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
            LocalNetworkAccessPermissionScreen(
                isRationale = permissionRequester.state is LocalNetworkPermissionState.Rationale,
                onConfirmClick = permissionRequester::onPermissionConfirmClick,
                onDismissClick = { navigator.goBack() },
            )
        }
    } else {
        content()
    }
}

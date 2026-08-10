/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
internal fun CollectionScreenRoot(
    id: CollectionId,
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (CollectionId) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val viewModel =
        assistedMetroViewModel<CollectionViewModel, CollectionViewModel.Factory> { create(id) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    scaffoldState.CollectionScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = dropUnlessResumed(block = onBackClick),
        onDeleteClick = dropUnlessResumed { onDeleteClick(id) },
        onEditClick = dropUnlessResumed { uiState.collection?.let { onEditClick(it) } },
        onSettingsClick = dropUnlessResumed(block = onSettingsClick),
        onFileClick = dropUnlessResumed(block = onFileClick),
        onFileInfoClick = dropUnlessResumed(block = onFileInfoClick),
        modifier = Modifier.testTag("CollectionScreenRoot"),
    )
}

package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBasicCollection
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowEmptyData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowLoadingData
import com.sorrowblue.comicviewer.framework.ui.rememberAppState
import com.sorrowblue.comicviewer.framework.ui.rememberCanonicalScaffoldLayoutState
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewMultiScreen
@Composable
private fun CollectionListScreenPreview(
    @PreviewParameter(
        CollectionListScreenPreviewParameterProvider::class
    ) config: Flow<PagingData<Collection>>,
) {
    PreviewTheme {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalAppState provides rememberAppState(sharedTransitionScope = this)) {
                AnimatedContent(true) {
                    if (it) {
                        val lazyPagingItems = config.collectAsLazyPagingItems()
                        val scaffoldState =
                            rememberCanonicalScaffoldLayoutState<Unit>(animatedContentScope = this)
                        CollectionListScreen(
                            scaffoldState = scaffoldState,
                            lazyPagingItems = lazyPagingItems,
                            lazyListState = rememberLazyListState(),
                            onContentsAction = {
                            },
                            onSettingsClick = {},
                            onCreateSmartCollectionClick = {},
                            onCreateBasicCollectionClick = {},
                        )
                    }
                }
            }
        }
    }
}

private class CollectionListScreenPreviewParameterProvider :
    PreviewParameterProvider<Flow<PagingData<Collection>>> {
    override val values
        get() = sequenceOf(
            PagingData.flowData<Collection>(20) {
                fakeBasicCollection(it)
            },
            PagingData.flowEmptyData(),
            PagingData.flowLoadingData(),
        )
}

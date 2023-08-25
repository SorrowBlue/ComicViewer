package com.sorrowblue.comicviewer.file.list

import androidx.compose.foundation.gestures.ScrollableState
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.pullrefresh.PullRefreshState

abstract class FileListState {

    abstract val isRefreshing: Boolean
    abstract val pullRefreshState: PullRefreshState

    abstract val scrollableState: ScrollableState
    abstract val lazyPagingItems: LazyPagingItems<File>
}

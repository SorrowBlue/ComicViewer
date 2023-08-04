package com.sorrowblue.comicviewer.folder

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.compose.FileInfoBottomSheet
import com.sorrowblue.comicviewer.folder.compose.FolderContents
import com.sorrowblue.comicviewer.folder.compose.FolderEmptyContent
import com.sorrowblue.comicviewer.folder.compose.FolderSearchSheet
import com.sorrowblue.comicviewer.folder.compose.FolderTopAppBar
import com.sorrowblue.comicviewer.folder.compose.PullRefreshIndicator
import com.sorrowblue.comicviewer.folder.compose.pullRefresh
import com.sorrowblue.comicviewer.folder.compose.rememberPullRefreshState
import com.sorrowblue.comicviewer.framework.compose.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun FolderScreen(
    navController: NavHostController,
    commonViewModel: CommonViewModel,
    modifier: Modifier = Modifier,
    viewModel: FolderViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scaffoldState =
        rememberBottomSheetScaffoldState(rememberStandardBottomSheetState(skipHiddenState = false))
    val query by viewModel.query.collectAsState()
    val title by viewModel.name.collectAsState()
    BackHandler(enabled = scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded) {
        scope.launch {
            scaffoldState.bottomSheetState.hide()
        }
    }
    LaunchedEffect(key1 = scaffoldState.bottomSheetState.targetValue) {
        when (scaffoldState.bottomSheetState.targetValue) {
            SheetValue.Hidden -> commonViewModel.isVisibleBottomNav.emit(true)
            SheetValue.Expanded -> commonViewModel.isVisibleBottomNav.emit(false)
            SheetValue.PartiallyExpanded -> commonViewModel.isVisibleBottomNav.emit(true)
        }
    }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var selectedFile: File? by remember { mutableStateOf(null) }
    if (openBottomSheet && selectedFile != null) {
        FileInfoBottomSheet(
            selectedFile!!,
            { openBottomSheet = false },
            viewModel::add,
            navController
        )
    }
    BottomSheetScaffold(
        topBar = {
            FolderTopAppBar(
                title = title,
                onClickSearch = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                onClickSortDisplay = {},
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        },
        sheetPeekHeight = 0.dp,
        sheetContent = {
            val range by viewModel.searchRange.collectAsState()
            val period by viewModel.searchPeriod.collectAsState()
            val order by viewModel.searchOrder.collectAsState()
            val sort by viewModel.searchSort.collectAsState()
            val lazyPagingItems = viewModel.searchPagingDataFlow.collectAsLazyPagingItems()
            FolderSearchSheet(
                query, viewModel::updateQuery,
                range, viewModel::updateSearchRange,
                period, viewModel::updateSearchPeriod,
                order, viewModel::updateSearchOrder,
                sort, viewModel::updateSearchSort,
                lazyPagingItems,
                { scope.launch { scaffoldState.bottomSheetState.hide() } },
                { file ->
                    when (file) {
                        is Book -> navController.navigate(
                            FolderFragmentDirections.actionFolderToBook().actionId,
                            BookFragmentArgs(file).toBundle()
                        )

                        is Folder -> navController.navigate(
                            FolderFragmentDirections.actionFolderSelf(
                                file.bookshelfId.value,
                                file.base64Path()
                            )
                        )
                    }

                }
            )
            LaunchedEffect(query, range, period, order, sort) {
                lazyPagingItems.refresh()
            }
        },
        sheetShape = RoundedCornerShape(0.dp),
        sheetSwipeEnabled = false,
        sheetDragHandle = {},
        scaffoldState = scaffoldState,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
        val isRefreshing =
            remember(pagingItems.loadState.refresh) { pagingItems.loadState.refresh is LoadState.Loading }
        val state = rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = { pagingItems.refresh() }
        )
        Box(Modifier.pullRefresh(state)) {
            if (pagingItems.isEmptyData) {
                FolderEmptyContent(
                    Modifier.fillMaxSize()
                )
            } else {
                FolderContents(
                    contentPadding = contentPadding,
                    pagingItems = pagingItems,
                    itemClick = {
                        when (it) {
                            is Book -> navController.navigate(
                                FolderFragmentDirections.actionFolderToBook().actionId,
                                BookFragmentArgs(it).toBundle()
                            )

                            is Folder -> navController.navigate(
                                FolderFragmentDirections.actionFolderSelf(
                                    it.bookshelfId.value,
                                    it.base64Path()
                                )
                            )
                        }
                    },
                    itemLongClick = {
                        selectedFile = it
                        openBottomSheet = true
                    }
                )
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = state,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

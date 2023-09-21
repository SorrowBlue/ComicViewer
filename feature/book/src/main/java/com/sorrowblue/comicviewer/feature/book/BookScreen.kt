package com.sorrowblue.comicviewer.feature.book

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.feature.book.section.BookBottomBar
import com.sorrowblue.comicviewer.feature.book.section.BookPage
import com.sorrowblue.comicviewer.feature.book.section.BookPager2
import com.sorrowblue.comicviewer.feature.book.section.BookPagerUiState
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

internal sealed interface BookScreenUiState {

    data object Loading : BookScreenUiState

    data class Empty(val book: Book) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val bookPagerUiState: BookPagerUiState,
        val isVisibleTooltip: Boolean,
    ) : BookScreenUiState
}

@Composable
internal fun BookRoute(
    onBackClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
    viewModel: BookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    BookScreen(
        uiState,
        onBackClick = onBackClick,
        onContainerClick = viewModel::toggleTooltip,
        onNextBookClick = onNextBookClick,
        onPageIndexChange = viewModel::updateLastReadPage
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun BookScreen(
    uiState: BookScreenUiState,
    onBackClick: () -> Unit,
    onPageIndexChange: (Int) -> Unit,
    onContainerClick: () -> Unit,
    onNextBookClick: (Book) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sys = rememberSystemUiController()
    DisposableEffect(Unit) {
        onDispose {
            sys.isSystemBarsVisible = true
        }
    }
    when (uiState) {
        is BookScreenUiState.Loaded -> {
            val currentList = remember(uiState.book.totalPageCount) {
                mutableStateListOf<BookPage>().apply {
                    addAll(getBookPageList(uiState.book.totalPageCount))
                }
            }
            val pagerState = rememberPagerState(
                initialPage = uiState.book.lastPageRead + 1,
                pageCount = { currentList.size }
            )

            sys.isSystemBarsVisible = uiState.isVisibleTooltip
            LaunchedEffect(pagerState.currentPage) {
                if (0 < pagerState.currentPage && pagerState.currentPage < uiState.book.totalPageCount) {
                    onPageIndexChange(pagerState.currentPage - 1)
                }
            }
            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = uiState.isVisibleTooltip,
                        enter = slideInVertically { -it },
                        exit = slideOutVertically { -it }) {
                        TopAppBar(
                            title = { Text(text = uiState.book.name) },
                            navigationIcon = {
                                IconButton(onClick = onBackClick) {
                                    Icon(Icons.TwoTone.ArrowBack, "Back")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    elevation = 3.0.dp
                                )
                            )
                        )
                    }
                },
                bottomBar = {
                    BookBottomBar(
                        uiState.isVisibleTooltip,
                        when (val a = currentList[pagerState.currentPage]) {
                            is BookPage.Next -> if (a.isNext) uiState.book.totalPageCount else 0
                            is BookPage.Split -> a.index
                        },
                        uiState.book.totalPageCount
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(it.toInt())
                        }
                    }
                }) {
                BookPager2(
                    uiState = uiState.bookPagerUiState,
                    pagerState = pagerState,
                    currentList = currentList,
                    onClick = onContainerClick,
                    onNextBookClick = onNextBookClick
                )
            }
        }

        BookScreenUiState.Loading -> {
            sys.isStatusBarVisible = true
            sys.isNavigationBarVisible = true
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BookScreenUiState.Empty -> {
            sys.isStatusBarVisible = true
            sys.isNavigationBarVisible = true
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = uiState.book.name) },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.TwoTone.ArrowBack, "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                elevation = 3.0.dp
                            )
                        )
                    )
                }
            ) {
                PreviewEmpty(modifier = Modifier.padding(it))
            }
        }
    }
}

private fun getBookPageList(totalPageCount: Int) =
    (0..<totalPageCount).map {
        when (it) {
            0 -> BookPage.Next(false)
            totalPageCount - 1 -> BookPage.Next(true)
            else -> BookPage.Split(it - 1, BookPage.Split.State.NOT_LOADED)
        }
    }

@Composable
fun PreviewEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppMaterialTheme.dimens.margin),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = com.sorrowblue.comicviewer.framework.compose.R.drawable.ic_undraw_faq_re_31cw),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Couldn't open the book", style = MaterialTheme.typography.headlineSmall)
    }
}

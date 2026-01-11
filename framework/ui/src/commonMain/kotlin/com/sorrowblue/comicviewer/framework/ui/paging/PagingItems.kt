package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : Any> rememberPagingItems(
    viewModel: PagingViewModel = viewModel { PagingViewModel() },
    block: @DisallowComposableCalls () -> Flow<PagingData<T>>,
): LazyPagingItems<T> = rememberRetained {
    block().cachedIn(viewModel.viewModelScope)
}.collectAsLazyPagingItems()

class PagingViewModel : ViewModel()

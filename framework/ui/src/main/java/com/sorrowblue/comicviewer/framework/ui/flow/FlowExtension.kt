package com.sorrowblue.comicviewer.framework.ui.flow

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sorrowblue.comicviewer.framework.ui.fragment.submitDataWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

context(AppCompatActivity)
fun <T> Flow<T>.launchInWithLifecycle() = flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

context(Fragment)
fun <T> Flow<T>.launchIn() = launchIn(viewLifecycleOwner.lifecycleScope)

context(Fragment)
fun <T> Flow<T>.launchInWithLifecycle() = flowWithLifecycle(viewLifecycleOwner.lifecycle).launchIn(viewLifecycleOwner.lifecycleScope)

context(Fragment)
fun <T : Any, VH : RecyclerView.ViewHolder> Flow<PagingData<T>>.attachAdapter(adapter: PagingDataAdapter<T, VH>) {
    viewLifecycleOwner.lifecycleScope.launch {
        collectLatest {
            adapter.submitDataWithLifecycle(it)
        }
    }
}

context (ViewModel)
fun <T> Flow<T>.mutableStateIn(init: T) = MutableStateFlow(init).also { mutable ->
    onEach { mutable.value = it }.launchIn(viewModelScope)
}

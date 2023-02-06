package com.sorrowblue.comicviewer.framework.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class PagingViewModel<T : Any> : ViewModel() {

    val isRefreshingFlow = MutableStateFlow(false)
    val isEmptyDataFlow = MutableStateFlow(false)
    var isInitialize = false

    abstract val pagingDataFlow: Flow<PagingData<T>>
    abstract val transitionName: String?
}

abstract class PagingFragment<T : Any>(contentLayoutId: Int) : FrameworkFragment(contentLayoutId) {

    protected abstract val viewModel: PagingViewModel<T>

    protected abstract val recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.transitionName != null) {
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
                scrimColor = MaterialColors.getColor(
                    requireContext(),
                    android.R.attr.colorBackground,
                    Color.TRANSPARENT
                )
                setPathMotion(MaterialArcMotion())
            }
            exitTransition = MaterialElevationScale(false)
            reenterTransition = MaterialElevationScale(true)
        } else {
            enterTransition = MaterialFadeThrough()
            exitTransition = MaterialFadeThrough()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        postponeEnterTransition()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        onCreateAdapter(adapter)
    }

    protected open fun onCreateAdapter(adapter: PagingDataAdapter<T, *>) {
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingDataFlow.collectLatest {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
        recyclerView.doOnPreDraw {
            if (viewModel.isInitialize) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(250)
                    startPostponedEnterTransition()
                }
            } else {
                viewModel.isInitialize = true
                startPostponedEnterTransition()
            }
        }
        adapter.loadStateFlow.map { it.refresh }.distinctUntilChanged()
            .onEach { viewModel.isRefreshingFlow.value = it is LoadState.Loading }
            .launchInWithLifecycle()
        adapter.loadStateFlow.map { it.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && adapter.itemCount == 0 }
            .distinctUntilChanged().onEach {
                viewModel.isEmptyDataFlow.value = it
            }.launchInWithLifecycle()
    }

    abstract val adapter: PagingDataAdapter<T, *>
}

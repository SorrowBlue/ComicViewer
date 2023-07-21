package com.sorrowblue.comicviewer.framework.ui.fragment

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.R
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface SupportPaging<T : Any> {
    val isRefreshingFlow: MutableStateFlow<Boolean>
    val isEmptyDataFlow: MutableStateFlow<Boolean>
    val pagingDataFlow: Flow<PagingData<T>>
    val transitionName: String?
}

abstract class PagingViewModel<T : Any> : ViewModel(), SupportPaging<T> {
    override val isRefreshingFlow = MutableStateFlow(false)
    override val isEmptyDataFlow = MutableStateFlow(false)
}

abstract class PagingAndroidViewModel<T : Any>(application: Application) :
    AndroidViewModel(application), SupportPaging<T> {

    protected val context: Context get() = getApplication()

    override val isRefreshingFlow = MutableStateFlow(false)
    override val isEmptyDataFlow = MutableStateFlow(false)
}

abstract class PagingFragment<T : Any, AD : PagingDataAdapter<T, *>> : FrameworkFragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    protected abstract val viewModel: SupportPaging<T>
    private val recyclerView: RecyclerView get() = requireView().requireViewById(R.id.recycler_view)

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
            exitTransition = MaterialElevationScale(false).apply {
                excludeTarget(com.google.android.material.R.id.search_view_scrim, true)
            }
            reenterTransition = MaterialElevationScale(true).apply {
                excludeTarget(com.google.android.material.R.id.search_view_scrim, true)
            }
        } else {
            enterTransition = MaterialFadeThrough().apply {
                excludeTarget(com.google.android.material.R.id.search_view_scrim, true)
            }
            exitTransition = MaterialFadeThrough().apply {
                excludeTarget(com.google.android.material.R.id.search_view_scrim, true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        recyclerView.adapter = pagingDataAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pagingDataFlow.collectLatest {
                    pagingDataAdapter.submitDataWithLifecycle(it)
                    (view.parent as? ViewGroup)?.doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
            }
        }
        pagingDataAdapter.loadStateFlow.map { it.refresh }.distinctUntilChanged()
            .onEach { viewModel.isRefreshingFlow.value = it is LoadState.Loading }
            .launchInWithLifecycle()
        pagingDataAdapter.loadStateFlow.map { it.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && pagingDataAdapter.itemCount == 0 }
            .distinctUntilChanged().onEach {
                viewModel.isEmptyDataFlow.value = it
            }.launchInWithLifecycle()
    }

    @Suppress("UNCHECKED_CAST")
    val pagingDataAdapter: AD
        get() = recyclerView.adapter as? AD ?: onCreatePagingDataAdapter()

    abstract fun onCreatePagingDataAdapter(): AD
}

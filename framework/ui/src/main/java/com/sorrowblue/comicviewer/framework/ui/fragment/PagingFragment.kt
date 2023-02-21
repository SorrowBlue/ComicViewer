package com.sorrowblue.comicviewer.framework.ui.fragment

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.AndroidViewModel
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
import com.sorrowblue.comicviewer.framework.ui.R
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import kotlinx.coroutines.delay
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
    var isInitialize: Boolean

    val pagingDataFlow: Flow<PagingData<T>>
    val transitionName: String?
}

abstract class PagingViewModel<T : Any> : ViewModel(), SupportPaging<T> {
    override val isRefreshingFlow = MutableStateFlow(false)
    override val isEmptyDataFlow = MutableStateFlow(false)
    override var isInitialize = false
}

abstract class PagingAndroidViewModel<T : Any>(application: Application) :
    AndroidViewModel(application), SupportPaging<T> {

    protected val context: Context get() = getApplication()

    override val isRefreshingFlow = MutableStateFlow(false)
    override val isEmptyDataFlow = MutableStateFlow(false)
    override var isInitialize = false
}

abstract class PagingFragment<T : Any>(contentLayoutId: Int) : FrameworkFragment(contentLayoutId) {

    protected abstract val viewModel: SupportPaging<T>

    private val recyclerView: RecyclerView get() = requireView().requireViewById<RecyclerView>(R.id.framework_ui_recycler_view)

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
                adapter.submitDataWithLifecycle(it)
            }
        }
        recyclerView.doOnPreDraw {
            if (viewModel.isInitialize) {
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(500)
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

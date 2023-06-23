package com.sorrowblue.comicviewer.file.list

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.file.R
import com.sorrowblue.comicviewer.file.info.FileInfoNavigation
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
abstract class FileListFragment(contentLayoutId: Int) : PagingFragment<File, FileListAdapter>(contentLayoutId) {

    private val toolbar: Toolbar get() = requireView().requireViewById(com.sorrowblue.comicviewer.framework.ui.R.id.toolbar)
    private val recyclerView: RecyclerView get() = requireView().requireViewById(com.sorrowblue.comicviewer.framework.ui.R.id.recycler_view)

    abstract override val viewModel: FileListViewModel

    abstract fun navigateToFile(
        file: File,
        transitionName: String,
        extras: FragmentNavigator.Extras
    )

    override fun onCreatePagingDataAdapter(): FileListAdapter {
        return FileListAdapter(
            runBlocking { viewModel.displayFlow.first() },
            runBlocking { viewModel.isEnabledThumbnailFlow.first() },
            { file, transitionName, extras -> navigateToFile(file, transitionName, extras) },
            { findNavController().navigate(FileInfoNavigation.getDeeplink(it)) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setupWithNavController()
        recyclerView.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding(horizontal = true, bottom = true)
            }
        }
        toolbar.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }
        viewModel.displayFlow.onEach {
            pagingDataAdapter.setDisplay(it)
        }.launchInWithLifecycle()

        var itemDecoration: AdaptiveSpacingItemDecoration? = null
        viewModel.displayFlow.combine(viewModel.columnSizeFlow) { display, columnSize ->
            when (display) {
                FolderDisplaySettings.Display.LIST -> {
                    itemDecoration?.let(recyclerView::removeItemDecoration)
                    recyclerView.setSpanCount(1)
                    recyclerView.requestApplyInsets()
                }

                FolderDisplaySettings.Display.GRID -> {
                    val spanCount = when (columnSize) {
                        FolderDisplaySettings.Size.SMALL -> R.integer.file_list_span_count_small
                        FolderDisplaySettings.Size.MEDIUM -> R.integer.file_list_span_count_medium
                        FolderDisplaySettings.Size.LARGE -> R.integer.file_list_span_count_large
                    }.let {
                        resources.getInteger(it)
                    }
                    recyclerView.setSpanCount(spanCount)
                    val itemSpace = when (columnSize) {
                        FolderDisplaySettings.Size.SMALL -> R.dimen.file_list_item_space_small
                        FolderDisplaySettings.Size.MEDIUM -> R.dimen.file_list_item_space_medium
                        FolderDisplaySettings.Size.LARGE -> R.dimen.file_list_item_space_medium
                    }.let(resources::getDimensionPixelSize)
                    itemDecoration?.let {
                        it.size = itemSpace
                        if (recyclerView.itemDecorationCount == 0) {
                            recyclerView.addItemDecoration(it)
                        } else {
                            recyclerView.invalidateItemDecorations()
                        }
                    }
                        ?: recyclerView.addItemDecoration(AdaptiveSpacingItemDecoration(itemSpace).also {
                            itemDecoration = it
                        })
                    recyclerView.requestApplyInsets()
                }
            }
        }.launchInWithLifecycle()
    }
}

package com.sorrowblue.comicviewer.readlater

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.paging.PagingDataAdapter
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.FolderAdapter
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import com.sorrowblue.comicviewer.readlater.databinding.ReadlaterFragmentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
internal class ReadLaterFragment : PagingFragment<File>(R.layout.readlater_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: ReadlaterFragmentBinding by viewBinding()

    override val viewModel: ReadLaterViewModel by viewModels()
    override val adapter: PagingDataAdapter<File, *>
        get() = FolderAdapter(
            runBlocking { viewModel.folderDisplaySettingsFlow.first().display },
            { file, transitionName, extras ->
                when (file) {
                    is Book -> navigate(
                        ReadLaterFragmentDirections.actionReadlaterToBook(file, transitionName),
                        extras
                    )

                    is Folder -> navigate(
                        ReadLaterFragmentDirections.actionReadlaterToFolder(
                            file,
                            transitionName
                        ),
                        extras
                    )
                }
            },
            { navigate("http://comicviewer.sorrowblue.com/file_info?server_id=${it.serverId.value}&path=${it.path.encodeBase64()}".toUri()) }
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController()
        binding.toolbar.setOnMenuItemClickListener(this)
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.frameworkUiRecyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }
    }

    override fun onCreateAdapter(adapter: PagingDataAdapter<File, *>) {
        super.onCreateAdapter(adapter)
        check(adapter is FolderAdapter)
        viewModel.folderDisplaySettingsFlow.onEach { adapter.display = it.display }
            .launchInWithLifecycle()
        viewModel.spanCountFlow.onEach(binding.frameworkUiRecyclerView::setSpanCount).launchInWithLifecycle()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> item.onNavDestinationSelected(findNavController())
        }
    }

    private fun ReadLaterFragmentDirections.Companion.actionReadlaterToBook(
        book: Book,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionReadlaterToBook().actionId
        override val arguments = BookFragmentArgs(
            book.serverId.value,
            book.path.encodeBase64(),
            transitionName,
            book.lastPageRead
        ).toBundle()
    }

    private fun ReadLaterFragmentDirections.Companion.actionReadlaterToFolder(
        folder: Folder,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionReadlaterToFolder().actionId
        override val arguments = FolderFragmentArgs(
            folder.serverId.value,
            folder.path.encodeBase64(),
            transitionName
        ).toBundle()
    }
}
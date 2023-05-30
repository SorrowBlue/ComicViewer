package com.sorrowblue.comicviewer.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.file.info.FileInfoNavigation
import com.sorrowblue.comicviewer.file.info.observeOpenFolder
import com.sorrowblue.comicviewer.file.list.FileListAdapter
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.history.databinding.HistoryFragmentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
internal class HistoryFragment : PagingFragment<File>(R.layout.history_fragment) {

    private val binding: HistoryFragmentBinding by viewBinding()
    override val viewModel: HistoryViewModel by viewModels()

    override val adapter
        get() = FileListAdapter(
            FolderDisplaySettings.Display.LIST,
            runBlocking { viewModel.isEnabledThumbnailFlow.first() },
            { file, transitionName, extras -> navigateToFile(file, transitionName, extras) },
            { findNavController().navigate(FileInfoNavigation.getDeeplink(it)) }
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOpenFolder(R.id.history_fragment) { bookshelfId, parent ->
            findNavController().navigate(
                HistoryFragmentDirections.actionHistoryToFolderNavigation().actionId,
                BookFragmentArgs(bookshelfId, parent).toBundle()
            )
        }

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController()
        binding.toolbar.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true)
                margin(top = true)
            }
        }

        binding.recyclerView.applyInsetter {
            type(systemBars = true, displayCutout = true) {
                padding(horizontal = true, bottom = true)
            }
        }
    }

    private fun navigateToFile(
        file: File,
        transitionName: String,
        extras: FragmentNavigator.Extras
    ) {
        when (file) {
            is Book -> findNavController().navigate(
                HistoryFragmentDirections.actionHistoryToBook(file, transitionName),
                extras
            )

            is Folder -> Unit
        }
    }

    private fun HistoryFragmentDirections.Companion.actionHistoryToBook(
        book: Book,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionHistoryToBook().actionId
        override val arguments = BookFragmentArgs(book, transitionName).toBundle()
    }
}

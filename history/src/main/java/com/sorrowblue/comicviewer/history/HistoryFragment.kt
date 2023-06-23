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
import com.sorrowblue.comicviewer.file.info.observeOpenFolder
import com.sorrowblue.comicviewer.file.list.FileListFragment
import com.sorrowblue.comicviewer.history.databinding.HistoryFragmentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class HistoryFragment : FileListFragment(R.layout.history_fragment) {

    private val binding: HistoryFragmentBinding by viewBinding()
    override val viewModel: HistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOpenFolder(R.id.history_fragment) { bookshelfId, parent ->
            findNavController().navigate(
                HistoryFragmentDirections.actionHistoryToFolderNavigation().actionId,
                BookFragmentArgs(bookshelfId, parent).toBundle()
            )
        }

        binding.viewModel = viewModel
    }

    override fun navigateToFile(
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

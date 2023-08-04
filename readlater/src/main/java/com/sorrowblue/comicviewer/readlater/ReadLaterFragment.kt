package com.sorrowblue.comicviewer.readlater

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
import com.sorrowblue.comicviewer.file.info.observeOpenFolder2
import com.sorrowblue.comicviewer.file.list.FileListFragment
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.readlater.databinding.ReadlaterFragmentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ReadLaterFragment : FileListFragment(R.layout.readlater_fragment) {

    private val binding: ReadlaterFragmentBinding by viewBinding()
    override val viewModel: ReadLaterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        observeOpenFolder2(R.id.readlater_fragment) { bookshelfId, parent ->
            findNavController().navigate(
                ReadLaterFragmentDirections.actionReadlaterToFolder().actionId,
                FolderFragmentArgs(bookshelfId, parent).toBundle()
            )
        }
    }

    override fun navigateToFile(
        file: File,
        transitionName: String,
        extras: FragmentNavigator.Extras
    ) {
        when (file) {
            is Book -> findNavController().navigate(
                ReadLaterFragmentDirections.actionReadlaterToBook(file, transitionName),
                extras
            )

            is Folder -> findNavController().navigate(
                ReadLaterFragmentDirections.actionReadlaterToFolder(file, transitionName),
                extras
            )
        }
    }

    private fun ReadLaterFragmentDirections.Companion.actionReadlaterToBook(
        book: Book,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionReadlaterToBook().actionId
        override val arguments = BookFragmentArgs(book, transitionName).toBundle()
    }

    private fun ReadLaterFragmentDirections.Companion.actionReadlaterToFolder(
        folder: Folder,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionReadlaterToFolder().actionId
        override val arguments = FolderFragmentArgs(
            folder.bookshelfId.value,
            folder.base64Path(),
            transitionName
        ).toBundle()
    }
}

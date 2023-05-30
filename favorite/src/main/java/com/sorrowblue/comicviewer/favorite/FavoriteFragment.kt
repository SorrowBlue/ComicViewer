package com.sorrowblue.comicviewer.favorite

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentBinding
import com.sorrowblue.comicviewer.file.info.observeOpenFolder
import com.sorrowblue.comicviewer.file.list.FileListFragment
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class FavoriteFragment : FileListFragment(R.layout.favorite_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: FavoriteFragmentBinding by viewBinding()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override val viewModel: FavoriteViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeOpenFolder(R.id.favorite_fragment) { bookshelfId, parent ->
            findNavController().navigate(
                FavoriteFragmentDirections.actionFavoriteToFolder().actionId,
                FolderFragmentArgs(bookshelfId, parent).toBundle()
            )
        }
        binding.viewModel = viewModel
        binding.toolbar.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite_menu_edit -> {
                findNavController().navigate(
                    FavoriteFragmentDirections.actionFavoriteToFavoriteEdit(
                        viewModel.favoriteId.value
                    )
                )
                true
            }

            R.id.favorite_menu_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("このお気に入りを削除しますか？")
                    .setPositiveButton("削除") { _, _ ->
                        viewModel.delete {
                            commonViewModel.snackbarMessage.tryEmit(viewModel.titleFlow.value + "を削除しました。")
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }

            else -> item.onNavDestinationSelected(findNavController())
        }
    }

    override fun navigateToFile(
        file: File,
        transitionName: String,
        extras: FragmentNavigator.Extras
    ) {
        when (file) {
            is Book -> findNavController().navigate(
                FavoriteFragmentDirections.actionFavoriteToBook().actionId,
                BookFragmentArgs(file, transitionName).toBundle(),
                null,
                extras
            )

            is Folder -> findNavController().navigate(
                FavoriteFragmentDirections.actionFavoriteToFolder().actionId,
                FolderFragmentArgs(
                    file.bookshelfId.value,
                    file.base64Path(),
                    transitionName
                ).toBundle(),
                null,
                extras
            )
        }
    }
}

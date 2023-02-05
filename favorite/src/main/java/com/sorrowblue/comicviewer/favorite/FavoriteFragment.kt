package com.sorrowblue.comicviewer.favorite

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.book.BookFragmentArgs
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.favorite.databinding.FavoriteFragmentBinding
import com.sorrowblue.comicviewer.file.info.FileInfoFragmentArgs
import com.sorrowblue.comicviewer.folder.FolderAdapter
import com.sorrowblue.comicviewer.folder.FolderFragmentArgs
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.encodeBase64
import com.sorrowblue.comicviewer.framework.ui.fragment.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.type
import com.sorrowblue.comicviewer.framework.ui.widget.ktx.setSpanCount
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
internal class FavoriteFragment : PagingFragment<File>(R.layout.favorite_fragment),
    Toolbar.OnMenuItemClickListener {

    private val binding: FavoriteFragmentBinding by viewBinding()
    private val commonViewModel: CommonViewModel by activityViewModels()

    override val viewModel: FavoriteViewModel by viewModels()
    override val recyclerView: RecyclerView get() = binding.recyclerView
    override val adapter
        get() = FolderAdapter(
            runBlocking { viewModel.folderDisplaySettingsFlow.first().display },
            { file, transitionName, extras ->
                when (file) {
                    is Book -> navigate(
                        FavoriteFragmentDirections.actionFavoriteToBook(file, transitionName),
                        extras
                    )

                    is Folder -> navigate(
                        FavoriteFragmentDirections.actionFavoriteToFolder(
                            file,
                            transitionName
                        ),
                        extras
                    )
                }
            },
            { navigate(FavoriteFragmentDirections.actionFavoriteToFileInfo(it)) }
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.toolbar.setupWithNavController(findNavController())
        binding.toolbar.setOnMenuItemClickListener(this)
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

    override fun onCreateAdapter(adapter: PagingDataAdapter<File, *>) {
        super.onCreateAdapter(adapter)
        check(adapter is FolderAdapter)
        viewModel.folderDisplaySettingsFlow.onEach { adapter.display = it.display }
            .launchInWithLifecycle()

        viewModel.spanCountFlow.onEach(binding.recyclerView::setSpanCount).launchInWithLifecycle()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite_menu_edit -> {
                navigate(FavoriteFragmentDirections.actionFavoriteToFavoriteEdit(viewModel.favoriteId.value))
                true
            }

            R.id.favorite_menu_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("このお気に入りリストを削除しますか？")
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

    private fun FavoriteFragmentDirections.Companion.actionFavoriteToBook(
        book: Book,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionFavoriteToBook().actionId
        override val arguments = BookFragmentArgs(
            book.serverId.value,
            book.path.encodeBase64(),
            transitionName,
            book.lastPageRead
        ).toBundle()
    }

    private fun FavoriteFragmentDirections.Companion.actionFavoriteToFolder(
        folder: Folder,
        transitionName: String
    ) = object : NavDirections {
        override val actionId = actionFavoriteToFolder().actionId
        override val arguments = FolderFragmentArgs(
            folder.serverId.value,
            folder.path.encodeBase64(),
            transitionName
        ).toBundle()
    }

    private fun FavoriteFragmentDirections.Companion.actionFavoriteToFileInfo(file: File) =
        object : NavDirections {
            override val actionId = actionFavoriteToFileInfo().actionId
            override val arguments = FileInfoFragmentArgs(
                file.serverId.value,
                file.path.encodeBase64(),
            ).toBundle()
        }
}

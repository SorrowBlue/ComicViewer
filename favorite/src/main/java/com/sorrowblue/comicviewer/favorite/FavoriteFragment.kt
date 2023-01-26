package com.sorrowblue.comicviewer.favorite

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sorrowblue.comicviewer.bookshelf.AbstractBookshelfFragment
import com.sorrowblue.comicviewer.bookshelf.AbstractBookshelfViewModel
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.usecase.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.CommonViewModel
import com.sorrowblue.comicviewer.framework.ui.fragment.launchIn
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class FavoriteFragment : AbstractBookshelfFragment() {

    override val viewModel: FavoriteViewModel by viewModels()
    override val menuResId: Int = R.menu.favorite
    private val commonViewModel: CommonViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        viewModel.count.onEach {
            viewModel.subTitleFlow.value =
                resources.getQuantityString(R.plurals.favorite_label_count, it, it)
        }.launchIn()
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
                    .setTitle("このお気に入りリストを削除しますか？")
                    .setPositiveButton("削除") { _, _ ->
                        viewModel.delete {
                            commonViewModel.snackbarMessage.tryEmit(viewModel.title.value + "を削除しました。")
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
}

@HiltViewModel
internal class FavoriteViewModel @Inject constructor(
    bookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase,
    pagingFavoriteBookUseCase: PagingFavoriteBookUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    override val savedStateHandle: SavedStateHandle
) : AbstractBookshelfViewModel(bookshelfDisplaySettingsUseCase), SupportSafeArgs,
    DefaultLifecycleObserver {

    private val args: FavoriteFragmentArgs by navArgs()

    private val favoriteFlow = getFavoriteUseCase.source
    val favoriteId = FavoriteId(args.favoriteId)

    val title = favoriteFlow.mapNotNull { it.dataOrNull?.name }.stateIn { "" }
    val count = favoriteFlow.mapNotNull { it.dataOrNull?.count }.stateIn { 0 }
    override val transitionName: String? = null

    override val pagingDataFlow = pagingFavoriteBookUseCase.execute(
        PagingFavoriteBookUseCase.Request(PagingConfig(20), FavoriteId(args.favoriteId))
    ).cachedIn(viewModelScope)

    override val pagingQueryDataFlow = pagingFavoriteBookUseCase.execute(
        PagingFavoriteBookUseCase.Request(PagingConfig(20), FavoriteId(args.favoriteId))
    ).cachedIn(viewModelScope)

    override var position: Int = 0

    override val titleFlow = favoriteFlow.mapNotNull { it.dataOrNull?.name }.stateIn { "" }

    override val subTitleFlow = MutableStateFlow("")

    override fun onStart(owner: LifecycleOwner) {
        viewModelScope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
        }
    }

    init {
        viewModelScope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
        }
    }

    fun delete(done: () -> Unit) {
        viewModelScope.launch {
            deleteFavoriteUseCase.execute(DeleteFavoriteUseCase.Request(FavoriteId(args.favoriteId)))
                .collect()
            done()
        }
    }
}

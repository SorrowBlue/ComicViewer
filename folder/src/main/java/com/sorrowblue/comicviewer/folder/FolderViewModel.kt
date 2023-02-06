package com.sorrowblue.comicviewer.folder

import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class FolderViewModel @Inject constructor(
    pagingFileUseCase: PagingFileUseCase,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    getBookshelfFolderUseCase: GetBookshelfFolderUseCase,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : PagingViewModel<File>(), SupportSafeArgs {

    private val args: FolderFragmentArgs by navArgs()
    var position = args.position
    override val transitionName = args.transitionName

    private val serverFolderFlow = getBookshelfFolderUseCase.execute(
        GetBookshelfFolderUseCase.Request(
            BookshelfId(args.serverId),
            Base64.decode(args.path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
                .decodeToString()
        )
    ).mapNotNull { it.dataOrNull }

    val serverFlow = serverFolderFlow.map { it.bookshelf }.stateIn { null }

    private val folderFlow = serverFolderFlow.map { it.folder }.stateIn { null }

    val titleFlow = serverFlow.mapNotNull { it?.displayName }.stateIn { "" }

    val subTitleFlow = folderFlow.mapNotNull { it?.name }.stateIn { "" }

    override val pagingDataFlow = serverFolderFlow.flatMapLatest {
        pagingFileUseCase.execute(
            PagingFileUseCase.Request(PagingConfig(100), it.bookshelf, it.folder)
        )
    }.cachedIn(viewModelScope)
    var query = ""

    val folderDisplaySettingsFlow = manageFolderDisplaySettingsUseCase.settings

    val spanCountFlow = folderDisplaySettingsFlow.map {
        when (it.display) {
            FolderDisplaySettings.Display.GRID -> it.spanCount
            FolderDisplaySettings.Display.LIST -> 1
        }
    }
    val pagingQueryDataFlow = serverFolderFlow.flatMapLatest {
        pagingQueryFileUseCase.execute(
            PagingQueryFileUseCase.Request(PagingConfig(100), it.bookshelf) {
                query
            }
        )
    }.cachedIn(viewModelScope)

    fun fullScan(scanType: ScanType, done: (String) -> Unit) {
        val folder = folderFlow.value ?: return
        viewModelScope.launch {
            scanBookshelfUseCase.execute(ScanBookshelfUseCase.Request(folder, scanType))
                .first().dataOrNull?.let { done.invoke(it) }
        }
    }
}

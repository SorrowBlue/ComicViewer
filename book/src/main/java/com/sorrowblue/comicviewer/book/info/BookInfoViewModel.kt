package com.sorrowblue.comicviewer.book.info

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.entity.ServerFile
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.DeviceStorage
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.entity.server.Smb
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteListUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import logcat.logcat

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class BookInfoViewModel @Inject constructor(
    getServerFileUseCase: GetServerFileUseCase,
    private val getBookshelfUseCase: GetFileUseCase,
    private val getFavoriteListUseCase: GetFavoriteListUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    private val args: BookInfoDialogArgs by navArgs()

    init {
        logcat {
            "serverId=${args.serverId}, path=${
                Base64.decode(
                    args.path,
                    Base64.URL_SAFE or Base64.NO_WRAP
                )
            }"
        }
    }

    val serverFileFlow: StateFlow<ServerFile?> =
        getServerFileUseCase.execute(
            GetServerFileUseCase.Request(
                ServerId(args.serverId),
                Base64.decode(args.path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
                    .decodeToString()
            )
        )
            .mapNotNull { it.dataOrNull }
            .stateIn { null }

    val fileFlow = serverFileFlow.map { it?.file }.stateIn { null }

    val parent = fileFlow.map {
        logcat { "parent=${it?.parent}" }
        when (serverFileFlow.value?.server) {
            is DeviceStorage -> it?.parent?.let {
                Uri.decode(it)
            }?.toUri()?.pathSegments?.let {
                it.subList(it.indexOf("document") + 1, it.lastIndex + 1).joinToString("/")
            }
            is Smb -> it?.parent
            null -> null
        }
    }.stateIn { null }

    val folderFlow: StateFlow<Folder?> = serverFileFlow.filterNotNull().flatMapLatest {
        getBookshelfUseCase.execute(GetFileUseCase.Request(it.server.id, it.file.parent))
            .mapNotNull {
                it.dataOrNull as? Folder
            }
    }.stateIn { null }
}

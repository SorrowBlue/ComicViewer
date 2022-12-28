package com.sorrowblue.comicviewer.book.info

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.entity.BookFile
import com.sorrowblue.comicviewer.domain.entity.BookFolder
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.entity.ServerFile
import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.usecase.GetFileRequest
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileRequest
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext val context: Context,
    getServerFileUseCase: GetServerFileUseCase,
    private val getBookshelfUseCase: GetFileUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    private val args: BookInfoDialogArgs by navArgs()

    init {
        logcat { "serverId=${args.serverId}, path=${Base64.decode(args.path, Base64.URL_SAFE or Base64.NO_WRAP)}" }
    }

    val serverFileFlow: StateFlow<ServerFile?> =
        getServerFileUseCase.execute(GetServerFileRequest(ServerId(args.serverId), Base64.decode(args.path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP).decodeToString()))
            .mapNotNull { it.dataOrNull }
            .stateIn { null }

    val fileFlow = serverFileFlow.map { it?.file }.stateIn { null }

    val parent = fileFlow.map {
        logcat { "parent=${it?.parent}" }
        when (serverFileFlow.value?.server) {
            is Server.DeviceStorage -> it?.parent?.let {
                Uri.decode(it)
            }?.toUri()?.pathSegments?.let {
                it.subList(it.indexOf("document") + 1, it.lastIndex + 1).joinToString("/")
            }
            is Server.Smb -> it?.parent
            null -> null
        }
    }.stateIn { null }

    val bookshelfFlow: StateFlow<Bookshelf?> = serverFileFlow.filterNotNull().flatMapLatest {
        getBookshelfUseCase.execute(GetFileRequest(it.server.id, it.file.parent)).mapNotNull {
            it.dataOrNull as? Bookshelf
        }
    }.stateIn { null }
}

// content://com.android.providers.downloads.documents/tree/raw:/storage/emulated/0/Download/新しいフォルダー (5)/document/raw:/storage/emulated/0/Download/新しいフォルダー (5)

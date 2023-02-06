package com.sorrowblue.comicviewer.server.management.device

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.server.InternalStorage
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import com.sorrowblue.comicviewer.framework.ui.flow.mutableStateIn
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import com.sorrowblue.comicviewer.server.management.util.RequireValidator
import com.sorrowblue.comicviewer.server.management.util.isErrorFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class ServerManagementDeviceViewModel @Inject constructor(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerManagementDeviceFragmentArgs by navArgs()
    private val bookshelfFolderFlow =
        getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
            .map { it.dataOrNull }
    private val internalStorageFlow =
        bookshelfFolderFlow.map { it?.bookshelf as? InternalStorage }.stateIn { null }
    val folder = bookshelfFolderFlow.map { it?.folder }.stateIn { null }

    val isRegister = internalStorageFlow.map { it != null }.stateIn { false }

    val data = folder.map { it?.path?.toUri() }.mutableStateIn(null)
    val transitionName = args.transitionName
    val displayNameValidator = listOf(RequireValidator())

    val displayName = internalStorageFlow.mapNotNull { it?.displayName }.mutableStateIn("")
    val dir: StateFlow<String> = data.mapNotNull { it?.lastPathSegment?.removePrefix(":") }
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    val isError = combine(
        data,
        displayNameValidator.isErrorFlow(),
    ) { data, displayName ->
        data == null || displayName
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val isConnecting = MutableStateFlow(false)

    fun connect(function: () -> Unit) {
        val internalStorage = internalStorageFlow.value ?: kotlin.run {
            if (args.bookshelfId == -1) null else return
        }
        isConnecting.value = true
        if (isError.value) {
            isConnecting.value = false
            message.tryEmit("入力してください")
            return
        }
        val library = internalStorage?.copy(displayName = displayName.value)
            ?: InternalStorage(displayName.value)
        viewModelScope.launch {
            when (val res = registerBookshelfUseCase.execute(
                RegisterBookshelfUseCase.Request(
                    library,
                    data.value?.toString().orEmpty()
                )
            ).first()) {
                is Result.Error -> when (res.error) {
                    RegisterBookshelfError.InvalidAuth -> message.emit("アクセス権限がありません。")
                    RegisterBookshelfError.InvalidPath -> message.emit("フォルダが存在しません。")
                    RegisterBookshelfError.InvalidBookshelfInfo -> Unit
                }

                is Result.Exception -> {
                    if (res.cause is Unknown) {
                        logcat { "Error: ${(res.cause as Unknown).throws}" }
                    }
                }

                is Result.Success -> {
                    function.invoke()
                }
            }
            isConnecting.value = false
        }
    }

    val message = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)
}

package com.sorrowblue.comicviewer.bookshelf.manage.device

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
open class BookshelfDeviceEditViewModel @Inject constructor(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfManageDeviceFragmentArgs by navArgs()
    private val bookshelfFolderFlow =
        getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
            .map { it.dataOrNull }
    private val internalStorageFlow =
        bookshelfFolderFlow.map { it?.bookshelf as? InternalStorage }.stateIn { null }

    val state = MutableStateFlow(BookshelfEditState.NONE)
    val errorDisplayName = MutableStateFlow("")
    val displayName = MutableStateFlow("")
    val message = MutableStateFlow("")
    val data = MutableStateFlow<Uri?>(null)
    val dir: StateFlow<String> = data.mapNotNull {
        it?.lastPathSegment?.split(":")?.lastOrNull()
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    init {
        viewModelScope.launch {
            bookshelfFolderFlow.filterNotNull().onEach {
                displayName.value = it.bookshelf.displayName
            }.collect()
        }
    }

    fun updateDisplayName(str: String) {
        displayName.value = str
        if (str.isEmpty()) {
            errorDisplayName.value = "なにか入力してください"
        } else {
            errorDisplayName.value = ""
        }
    }


    fun connect(function: () -> Unit) {
        val internalStorage = internalStorageFlow.value ?: kotlin.run {
            if (args.bookshelfId == -1) null else return
        }

        state.value = BookshelfEditState.LOADING

        updateDisplayName(displayName.value)

        if (errorDisplayName.value.isNotEmpty()) {
            state.value = BookshelfEditState.NONE
            return
        }
        val storage = internalStorage?.copy(displayName = displayName.value)
            ?: InternalStorage(displayName.value)
        viewModelScope.launch {
            val resultFlow = registerBookshelfUseCase.execute(
                RegisterBookshelfUseCase.Request(storage, data.value?.toString().orEmpty())
            )
            when (val result = resultFlow.first()) {
                is Result.Error -> when (result.error) {
                    RegisterBookshelfError.InvalidAuth -> message.emit("アクセス権限がありません。")
                    RegisterBookshelfError.InvalidPath -> message.emit("フォルダが存在しません。")
                    RegisterBookshelfError.InvalidBookshelfInfo -> Unit
                }

                is Result.Exception -> {
                    if (result.cause is Unknown) {
                        logcat { "Error: ${(result.cause as Unknown).throws}" }
                    }
                }

                is Result.Success -> {
                    function.invoke()
                }
            }
            state.value = BookshelfEditState.NONE
        }
    }
}

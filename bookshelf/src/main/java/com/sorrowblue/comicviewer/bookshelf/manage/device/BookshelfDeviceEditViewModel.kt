package com.sorrowblue.comicviewer.bookshelf.manage.device

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.onSuccess
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

fun <D, E : Resource.AppError> Flow<Resource<D, E>>.error(function: suspend (E) -> Unit): Flow<Resource<D, E>> {
    onEach {
        if (it is Resource.Error) {
            function.invoke(it.error)
        }
    }
    return this
}

@HiltViewModel
open class BookshelfDeviceEditViewModel @Inject constructor(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfManageDeviceFragmentArgs by navArgs()

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
            getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
                .first().onSuccess {
                    data.value = it.folder.path.toUri()
                    displayName.value = it.bookshelf.displayName
                }
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

        state.value = BookshelfEditState.LOADING

        updateDisplayName(displayName.value)

        if (errorDisplayName.value.isNotEmpty()) {
            state.value = BookshelfEditState.NONE
            return
        }
        val storage = InternalStorage(BookshelfId(args.bookshelfId), displayName.value, 0)
        viewModelScope.launch {
            val resultFlow = registerBookshelfUseCase.execute(
                RegisterBookshelfUseCase.Request(storage, data.value?.toString().orEmpty())
            )
            when (val result = resultFlow.first()) {
                is Resource.Error -> {
                    logcat { "result=$result" }
                    message.emit("フォルダが存在しません。")
                }

                is Resource.Success -> {
                    logcat { "result=$result" }
                    function.invoke()
                }
            }
            state.value = BookshelfEditState.NONE
        }
    }
}

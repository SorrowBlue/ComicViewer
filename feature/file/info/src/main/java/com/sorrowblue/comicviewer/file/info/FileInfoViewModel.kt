package com.sorrowblue.comicviewer.file.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.file.info.navigation.FileInfoArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
internal class FileInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFileUseCase: GetFileUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
) : ViewModel() {
    fun onAddReadLaterClick(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(
                AddReadLaterUseCase.Request(
                    bookshelfId = file.bookshelfId,
                    path = file.path
                )
            ).first()
        }
    }

    private val args = FileInfoArgs(savedStateHandle)
    private val _file = MutableStateFlow<File?>(null)
    val file = _file.asStateFlow()

    val isVisibleOpenFolder = args.isVisibleOpenFolder

    init {
        getFileUseCase.execute(GetFileUseCase.Request(args.bookshelfId, args.path)).onSuccess {
            _file.value = it
        }.launchIn(viewModelScope)
    }
}

private fun <V, T : Resource<V, *>> Flow<T>.onSuccess(action: (V) -> Unit) = onEach {
    if (it is Resource.Success<*>) {
        action(it.data as V)
    }
}

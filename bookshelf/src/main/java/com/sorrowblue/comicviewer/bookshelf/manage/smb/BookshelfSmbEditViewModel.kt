package com.sorrowblue.comicviewer.bookshelf.manage.smb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val portRegex =
    "^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))\$".toRegex()
private val hostRegex =
    "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])\$".toRegex()

@HiltViewModel
internal open class BookshelfSmbEditViewModel @Inject constructor(
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    sealed interface UiEvent {
        data class Error(val e: String) : UiEvent
        data object SaveComplete : UiEvent
    }

    sealed interface UiState {
        data object NONE : UiState
        data object CONNECTING : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.NONE)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(0, 1, BufferOverflow.DROP_OLDEST)
    val uiEvent = _uiEvent.asSharedFlow()

    private val args: BookshelfManageSmbFragmentArgs by navArgs()

    val host = Form(singleLine = true) { !it.matches(hostRegex) }
    val port = Form("445", singleLine = true) { !it.matches(portRegex) }
    val path = Form(singleLine = true) { it.isEmpty() }
    val displayName = Form(singleLine = true) { it.isEmpty() }
    val isGuest = Form2(true)

    val domain = Form(singleLine = true) { it.isEmpty() }
    val username = Form(singleLine = true) { it.isEmpty() }
    val password = Form(singleLine = true) { it.isEmpty() }

    init {
        viewModelScope.launch {
            val bookshelfFolder =
                getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
                    .map { it.dataOrNull }.filter { it?.bookshelf is SmbServer }.first()
                    ?: return@launch
            val server = bookshelfFolder.bookshelf as SmbServer
            val folder = bookshelfFolder.folder
            host.edit(server.host)
            port.edit(server.port.toString())
            path.edit(folder.path)
            displayName.edit(server.displayName)
            when (val auth = server.auth) {
                SmbServer.Auth.Guest -> {
                    isGuest.edit(true)
                }

                is SmbServer.Auth.UsernamePassword -> {
                    isGuest.edit(false)
                    domain.edit(auth.domain)
                    username.edit(auth.username)
                    password.edit(auth.password)

                }
            }
        }
    }

    fun save() {
        _uiState.value = UiState.CONNECTING
        host.validate()
        port.validate()
        path.validate()
        displayName.validate()
        if (!isGuest.flow.value) {
            domain.validate()
            username.validate()
            password.validate()
        }
        viewModelScope.launch {
            if (
                host.isError.first() || port.isError.first() || path.isError.first() || displayName.isError.first()
                || (isGuest.flow.value && (username.isError.first() || password.isError.first()))
            ) {
                _uiState.value = UiState.NONE
                _uiEvent.emit(UiEvent.Error("入力エラー"))
                return@launch
            }
            val auth = if (isGuest.flow.value) {
                SmbServer.Auth.Guest
            } else {
                SmbServer.Auth.UsernamePassword(
                    domain.flow.value,
                    username.flow.value,
                    password.flow.value
                )
            }
            val smbServer = SmbServer(
                id = BookshelfId(args.bookshelfId),
                displayName = displayName.flow.value,
                host = host.flow.value,
                port = port.flow.value.toInt(),
                auth = auth,
                fileCount = 0
            )
            when (val res = registerBookshelfUseCase.execute(
                RegisterBookshelfUseCase.Request(
                    smbServer, if (path.flow.value.isEmpty()) "/" else {
                        ("/${path.flow.value}/").replace("(/+)".toRegex(), "/")
                    }
                )
            ).first()) {
                is com.sorrowblue.comicviewer.framework.Result.Error -> {
                    when (res.error) {
                        RegisterBookshelfError.InvalidAuth -> _uiEvent.emit(UiEvent.Error("無効な認証です"))
                        RegisterBookshelfError.InvalidBookshelfInfo -> _uiEvent.emit(UiEvent.Error("このサイトにクセスできません(${smbServer.host}${path} )。"))
                        RegisterBookshelfError.InvalidPath -> _uiEvent.emit(UiEvent.Error("無効なパス"))
                        RegisterBookshelfError.Network -> _uiEvent.emit(UiEvent.Error("ネットワークに接続されていません"))
                        RegisterBookshelfError.Unknown -> _uiEvent.emit(UiEvent.Error("不明なエラー"))
                    }
                }

                is com.sorrowblue.comicviewer.framework.Result.Exception -> {
                    _uiEvent.emit(UiEvent.Error("不明なエラー"))
                }

                is com.sorrowblue.comicviewer.framework.Result.Success -> {
                    _uiEvent.emit(UiEvent.SaveComplete)
                }
            }
            _uiState.value = UiState.NONE
        }
    }
}

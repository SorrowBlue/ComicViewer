package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.onError
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.AuthMethod
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookshelfEditViewModel @Inject constructor(
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = BookshelfEditArgs(savedStateHandle)
    private val _uiState =
        MutableStateFlow<BookshelfEditScreenUiState>(BookshelfEditScreenUiState.Loading)
    val uiState: StateFlow<BookshelfEditScreenUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        if (0 < args.bookshelfId.value) {
            fetchBookshelfFolder(args.bookshelfId)
        } else {
            initUiState(args.bookshelfType)
        }
    }

    private fun initUiState(type: BookshelfType) {
        _uiState.value = BookshelfEditScreenUiState.Editing(
            mode = EditMode.Register,
            running = false,
            editorUiState = when (type) {
                BookshelfType.SMB -> BookshelfEditorUiState.SmbServer(
                    displayName = "",
                    isDisplayNameError = false,
                    host = "",
                    isHostError = false,
                    port = "",
                    isPortError = false,
                    path = "",
                    authMethod = AuthMethod.GUEST,
                    domain = "",
                    username = "",
                    isUsernameError = false,
                    password = "",
                    isPasswordError = false
                )

                BookshelfType.DEVICE -> BookshelfEditorUiState.DeviceStorage(
                    displayName = "",
                    isDisplayNameError = false,
                    dir = "",
                    validate = false
                )
            }
        )
    }

    private fun fetchBookshelfFolder(bookshelfId: BookshelfId) {
        viewModelScope.launch {
            getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(bookshelfId))
                .first().onSuccess {
                    _uiState.value = BookshelfEditScreenUiState.Editing(
                        mode = EditMode.Change,
                        running = false,
                        editorUiState = when (val bookshelf = it.bookshelf) {
                            is InternalStorage ->
                                BookshelfEditorUiState.DeviceStorage(
                                    displayName = bookshelf.displayName,
                                    isDisplayNameError = false,
                                    dir = it.folder.path,
                                    validate = false
                                )

                            is SmbServer ->
                                when (val auth = bookshelf.auth) {
                                    SmbServer.Auth.Guest -> BookshelfEditorUiState.SmbServer(
                                        displayName = bookshelf.displayName,
                                        isDisplayNameError = false,
                                        host = bookshelf.host,
                                        isHostError = false,
                                        port = bookshelf.port.toString(),
                                        isPortError = false,
                                        path = it.folder.path.removeSurrounding("/"),
                                        authMethod = AuthMethod.GUEST,
                                        domain = "",
                                        username = "",
                                        isUsernameError = false,
                                        password = "",
                                        isPasswordError = false
                                    )

                                    is SmbServer.Auth.UsernamePassword -> BookshelfEditorUiState.SmbServer(
                                        displayName = bookshelf.displayName,
                                        isDisplayNameError = false,
                                        host = bookshelf.host,
                                        isHostError = false,
                                        port = bookshelf.port.toString(),
                                        isPortError = false,
                                        path = it.folder.path.removeSurrounding("/"),
                                        authMethod = AuthMethod.USERPASS,
                                        domain = auth.domain,
                                        username = auth.username,
                                        isUsernameError = false,
                                        password = auth.password,
                                        isPasswordError = false
                                    )
                                }
                        }
                    )
                }
        }
    }

    fun onDisplayNameChanged(text: String) {
        if (text.isBlank()) {
            updateEditing(
                onDevice = {
                    it.copy(
                        displayName = text,
                        isDisplayNameError = true,
                        validate = it.dir.isNotBlank() && text.isNotBlank()
                    )
                },
                onSmb = { it.copy(displayName = text, isDisplayNameError = true) }
            )
        } else {
            updateEditing(
                onDevice = {
                    it.copy(
                        displayName = text,
                        isDisplayNameError = false,
                        validate = it.dir.isNotBlank() && text.isNotBlank()
                    )
                },
                onSmb = { it.copy(displayName = text, isDisplayNameError = false) }
            )
        }
    }

    fun onHostChanged(text: String) {
        if (!hostRegex.matches(text)) {
            updateEditing(onSmb = { it.copy(host = text, isHostError = true) })
        } else {
            updateEditing(onSmb = { it.copy(host = text, isHostError = false) })
        }
    }

    fun onPortChanged(text: String) {
        if (!portRegex.matches(text)) {
            updateEditing(onSmb = { it.copy(port = text, isPortError = true) })
        } else {
            updateEditing(onSmb = { it.copy(port = text, isPortError = false) })
        }
    }

    fun onPathChange(text: String) {
        updateEditing(onSmb = { it.copy(path = text) })
    }

    fun onAuthMethodChange(authMethod: AuthMethod) {
        updateEditing(onSmb = { it.copy(authMethod = authMethod) })
    }

    fun onDomainChange(text: String) {
        updateEditing(onSmb = { it.copy(domain = text) })
    }

    fun onUsernameChange(text: String) {
        if (text.isEmpty()) {
            updateEditing(onSmb = { it.copy(username = text, isUsernameError = true) })
        } else {
            updateEditing(onSmb = { it.copy(username = text, isUsernameError = false) })
        }
    }

    fun onPasswordChange(text: String) {
        if (text.isEmpty()) {
            updateEditing(onSmb = { it.copy(password = text, isPasswordError = true) })
        } else {
            updateEditing(onSmb = { it.copy(password = text, isPasswordError = false) })
        }
    }

    private fun validateAll(): Boolean {
        var uiState = this.uiState.value
        if (uiState !is BookshelfEditScreenUiState.Editing) return false
        when (val editorUiState = uiState.editorUiState) {
            is BookshelfEditorUiState.DeviceStorage ->
                onDisplayNameChanged(editorUiState.displayName)

            is BookshelfEditorUiState.SmbServer -> {
                onDisplayNameChanged(editorUiState.displayName)
                onHostChanged(editorUiState.host)
                onPortChanged(editorUiState.port)
                onPathChange(editorUiState.path)
                when (editorUiState.authMethod) {
                    AuthMethod.GUEST -> Unit
                    AuthMethod.USERPASS -> {
                        onUsernameChange(editorUiState.username)
                        onPasswordChange(editorUiState.password)
                    }
                }
            }
        }
        uiState = this.uiState.value
        if (uiState !is BookshelfEditScreenUiState.Editing) return false
        return when (val editorUiState = uiState.editorUiState) {
            is BookshelfEditorUiState.DeviceStorage ->
                !editorUiState.isDisplayNameError && deviceStorageUri != null

            is BookshelfEditorUiState.SmbServer -> {
                !editorUiState.isDisplayNameError && !editorUiState.isHostError
                        && !editorUiState.isPortError
                        && when (editorUiState.authMethod) {
                    AuthMethod.GUEST -> true
                    AuthMethod.USERPASS -> !editorUiState.isUsernameError && !editorUiState.isPasswordError
                }
            }
        }
    }

    fun save() {
        if (!validateAll()) {
            _uiEvents.tryEmit(UiEvent.ShowSnackbar("入力内容を確認してください。"))
            return
        }
        val uiState = uiState.value
        if (uiState is BookshelfEditScreenUiState.Editing) {
            _uiState.value = uiState.copy(running = true)
            val bookshelf = when (val editorUiState = uiState.editorUiState) {
                is BookshelfEditorUiState.DeviceStorage ->
                    InternalStorage(args.bookshelfId, editorUiState.displayName)

                is BookshelfEditorUiState.SmbServer -> {
                    onDisplayNameChanged(editorUiState.displayName)
                    onHostChanged(editorUiState.host)
                    onPortChanged(editorUiState.port)
                    SmbServer(
                        args.bookshelfId,
                        editorUiState.displayName,
                        editorUiState.host,
                        editorUiState.port.toInt(),
                        when (editorUiState.authMethod) {
                            AuthMethod.GUEST -> SmbServer.Auth.Guest
                            AuthMethod.USERPASS -> SmbServer.Auth.UsernamePassword(
                                editorUiState.domain,
                                editorUiState.username,
                                editorUiState.password
                            )
                        }
                    )
                }
            }
            val path = when (val editorUiState = uiState.editorUiState) {
                is BookshelfEditorUiState.DeviceStorage -> deviceStorageUri?.toString().orEmpty()
                is BookshelfEditorUiState.SmbServer -> if (editorUiState.path.isEmpty()) "/" else {
                    ("/${editorUiState.path}/").replace("(/+)".toRegex(), "/")
                }
            }
            viewModelScope.launch {
                registerBookshelfUseCase.execute(RegisterBookshelfUseCase.Request(bookshelf, path))
                    .first().onSuccess {
                        _uiState.value = BookshelfEditScreenUiState.Complete
                    }.onError {
                        _uiState.value = uiState.copy(running = false)
                        _uiEvents.emit(
                            UiEvent.ShowSnackbar(
                                when (it) {
                                    RegisterBookshelfUseCase.Error.Auth -> "認証エラー"
                                    RegisterBookshelfUseCase.Error.Host -> "無効なホスト"
                                    RegisterBookshelfUseCase.Error.Network -> "無効なネットワーク"
                                    RegisterBookshelfUseCase.Error.Path -> "無効なパス"
                                    RegisterBookshelfUseCase.Error.System -> "システムエラー"
                                }
                            )
                        )
                    }
            }
        }
    }

    private var deviceStorageUri: Uri? = null

    fun updateUri(uri: Uri) {
        deviceStorageUri = uri
        updateEditing(
            onDevice = {
                val dir = uri.lastPathSegment?.split(":")?.lastOrNull().orEmpty()
                it.copy(dir = dir, validate = dir.isNotBlank() && it.displayName.isNotBlank())
            }
        )
    }

    private fun updateEditing(
        onDevice: (BookshelfEditorUiState.DeviceStorage) -> BookshelfEditorUiState = { it },
        onSmb: (BookshelfEditorUiState.SmbServer) -> BookshelfEditorUiState = { it },
    ) {
        val uiState = _uiState.value
        if (uiState is BookshelfEditScreenUiState.Editing) {
            _uiState.value = uiState.copy(
                editorUiState = when (val editorUiState: BookshelfEditorUiState =
                    uiState.editorUiState) {
                    is BookshelfEditorUiState.DeviceStorage -> onDevice(editorUiState)
                    is BookshelfEditorUiState.SmbServer -> onSmb(editorUiState)
                }
            )
        }
    }

    private val portRegex =
        "^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{1,5})|([0-9]{1,4}))\$".toRegex()


    private val hostRegex =
        "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])\$".toRegex()
}

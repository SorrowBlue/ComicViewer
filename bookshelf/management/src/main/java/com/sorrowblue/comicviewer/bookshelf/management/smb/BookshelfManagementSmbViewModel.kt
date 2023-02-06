package com.sorrowblue.comicviewer.bookshelf.management.smb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.bookshelf.management.util.DomainNameTextValidator
import com.sorrowblue.comicviewer.bookshelf.management.util.HostNameTextValidator
import com.sorrowblue.comicviewer.bookshelf.management.util.PortTextValidator
import com.sorrowblue.comicviewer.bookshelf.management.util.RequireValidator
import com.sorrowblue.comicviewer.bookshelf.management.util.isErrorFlow
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.server.SmbServer
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.ui.flow.mutableStateIn
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class BookshelfManagementSmbViewModel @Inject constructor(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfManagementSmbFragmentArgs by navArgs()
    private val bookshelfFolderFlow =
        getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
            .map { it.dataOrNull }
    private val smbServerFlow =
        bookshelfFolderFlow.map { it?.bookshelf as? SmbServer }.stateIn { null }
    val folder = bookshelfFolderFlow.map { it?.folder }.stateIn { null }

    val isAdd = smbServerFlow.map { it != null }.stateIn { false }

    val transitionName = args.transitionName

    val hostFlow = smbServerFlow.mapNotNull { it?.host }.mutableStateIn("")
    val portFlow = smbServerFlow.mapNotNull { it?.port }.mutableStateIn(445)
    val pathFlow =
        folder.mapNotNull { it?.path?.removePrefix("/")?.removeSuffix("/") }.mutableStateIn("")
    val displayNameFlow = smbServerFlow.mapNotNull { it?.displayName }.mutableStateIn("")
    val isGuestFlow = MutableStateFlow(false)
    val domainFlow = smbServerFlow.mapNotNull { it?.domain }.mutableStateIn("")
    val usernameFlow = smbServerFlow.mapNotNull { it?.username }.mutableStateIn("")
    val passwordFlow = smbServerFlow.mapNotNull { it?.password }.mutableStateIn("")

    val hostValidations = listOf(RequireValidator(), HostNameTextValidator())
    val portValidations = listOf(RequireValidator(), PortTextValidator())
    val domainValidator = listOf(DomainNameTextValidator())
    val usernameValidator = listOf(RequireValidator())
    val passwordValidator = listOf(RequireValidator())
    val isError = combine(
        isGuestFlow,
        (hostValidations + portValidations).isErrorFlow(),
        (hostValidations + portValidations + domainValidator + usernameValidator + passwordValidator).isErrorFlow(),
    ) { isGuest, onGuest, onAuth ->
        if (isGuest) onGuest else onAuth
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isConnecting = MutableStateFlow(false)

    fun connect(done: (Boolean) -> Unit) {
        val _smbServer = smbServerFlow.value ?: kotlin.run {
            if (args.bookshelfId == -1) null else return
        }
        isConnecting.value = true
        if (isError.value) {
            isConnecting.value = false
            message.tryEmit("入力してください")
            return
        }
        val path = if (pathFlow.value.isEmpty()) "/" else {
            ("/${pathFlow.value}/").replace("(/+)".toRegex(), "/")
        }
        val port = portFlow.value
        val auth =
            if (isGuestFlow.value) SmbServer.Auth.Guest
            else SmbServer.Auth.UsernamePassword(
                domainFlow.value,
                usernameFlow.value,
                passwordFlow.value
            )
        val smbServer = _smbServer?.copy(
            displayName = displayNameFlow.value,
            host = hostFlow.value,
            port = port,
            auth = auth
        ) ?: SmbServer(
            displayName = displayNameFlow.value,
            host = hostFlow.value,
            port = port,
            auth = auth
        )
        viewModelScope.launch {
            when (val res =
                registerBookshelfUseCase.execute(RegisterBookshelfUseCase.Request(smbServer, path))
                    .first()) {
                is Result.Error -> {
                    logcat(tag = this@BookshelfManagementSmbViewModel::class.simpleName) { "Error: ${res.error}" }
                    when (res.error) {
                        RegisterBookshelfError.InvalidAuth -> message.emit("この設定ではアクセスできません")
                        RegisterBookshelfError.InvalidBookshelfInfo -> message.emit("このサイトにクセスできません(${smbServer.host}${path} )。")
                        RegisterBookshelfError.InvalidPath -> message.emit("不明なエラー")
                    }
                }

                is Result.Exception -> logcat(tag = this@BookshelfManagementSmbViewModel::class.simpleName) { "Error: ${res.cause}" }
                is Result.Success -> {
                    logcat(tag = this@BookshelfManagementSmbViewModel::class.simpleName) { "Success: $smbServer" }
                    done.invoke(args.bookshelfId == -1)
                }
            }
            isConnecting.value = false
        }
    }

    val message = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)


    private val SmbServer.domain
        get() = when (val smbAuth = auth) {
            SmbServer.Auth.Guest -> ""
            is SmbServer.Auth.UsernamePassword -> smbAuth.domain
        }

    private val SmbServer.username
        get() = when (val smbAuth = auth) {
            SmbServer.Auth.Guest -> ""
            is SmbServer.Auth.UsernamePassword -> smbAuth.username
        }

    private val SmbServer.password
        get() = when (val smbAuth = auth) {
            SmbServer.Auth.Guest -> ""
            is SmbServer.Auth.UsernamePassword -> smbAuth.password
        }
}

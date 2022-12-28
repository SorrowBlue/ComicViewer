package com.sorrowblue.comicviewer.server.management.smb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.entity.RegisterServerRequest
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryError
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.server.management.util.HostNameTextValidator
import com.sorrowblue.comicviewer.server.management.util.RequireValidator
import com.sorrowblue.comicviewer.server.management.util.isErrorFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat


@HiltViewModel
internal class ServerManagementSmbViewModel @Inject constructor(
    private val registerLibraryUseCase: RegisterLibraryUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerManagementSmbFragmentArgs by navArgs()
    private val smb: Server.Smb? = args.serverSmb
    private val bookshelf: Bookshelf? = args.bookshelf

    val isAdd: Boolean = args.serverSmb == null

    val transitionName = args.transitionName

    val host = MutableStateFlow(smb?.host.orEmpty())
    val port = MutableStateFlow(smb?.port.orEmpty())
    val path = MutableStateFlow(bookshelf?.path.orEmpty().removeSuffix("/"))
    val displayName = MutableStateFlow(smb?.displayName.orEmpty())
    val isGuest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val domain = MutableStateFlow(smb?.domain.orEmpty())
    val username = MutableStateFlow(smb?.username.orEmpty())
    val password = MutableStateFlow(smb?.password.orEmpty())

    val hostValidations = listOf(RequireValidator(), HostNameTextValidator())
    val portValidations = listOf(RequireValidator(), HostNameTextValidator())
    val usernameValidator = listOf(RequireValidator())
    val passwordValidator = listOf(RequireValidator())
    val isError = combine(
        isGuest,
        hostValidations.isErrorFlow(),
        usernameValidator.isErrorFlow(),
        passwordValidator.isErrorFlow()
    ) { isGuest, host, username, password ->
        if (isGuest) host else host || username || password
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isConnecting = MutableStateFlow(false)

    fun connect(done: (Boolean) -> Unit) {
        isConnecting.value = true
        if (isError.value) {
            isConnecting.value = false
            message.tryEmit("入力してください")
            return
        }
        val newPath = if (path.value.isEmpty()) "/" else {
            ("/${path.value}/").replace("(/+)".toRegex(), "/")
        }
        val auth =
            if (isGuest.value) Server.Smb.Auth.Guest
            else Server.Smb.Auth.UsernamePassword("", username.value, password.value)
        val smb1 = smb?.copy(
            displayName = displayName.value,
            host = host.value,
            auth = auth
        ) ?: Server.Smb(
            displayName = displayName.value,
            host = host.value,
            port = "433",
            auth = auth
        )
        viewModelScope.launch {
            when (val res = registerLibraryUseCase.execute(RegisterServerRequest(smb1, newPath))) {
                is Result.Error -> {
                    logcat(tag = this@ServerManagementSmbViewModel::class.simpleName) { "Error: ${res.error}" }
                    when (res.error) {
                        RegisterLibraryError.NO_EXISTS -> message.emit("このサイトにクセスできません(${smb1.host}${newPath} )。")
                        RegisterLibraryError.LOGON_FAILURE -> message.emit("この設定ではアクセスできません")
                        RegisterLibraryError.UNKNOWN -> message.emit("不明なエラー")
                        RegisterLibraryError.BAD_NETWORK_NAME -> message.emit("ネットワーク名を確認してください。")
                    }
                }
                is Result.Exception -> logcat(tag = this@ServerManagementSmbViewModel::class.simpleName) { "Error: ${res.cause}" }
                is Result.Success -> {
                    logcat(tag = this@ServerManagementSmbViewModel::class.simpleName) { "Success: ${smb1}" }
                    done.invoke(args.serverSmb == null)
                }
            }
            isConnecting.value = false
        }
    }

    val message = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)


    private val Server.Smb.domain
        get() = when (val smbAuth = auth) {
            Server.Smb.Auth.Guest -> ""
            is Server.Smb.Auth.UsernamePassword -> smbAuth.domain
        }

    private val Server.Smb.username
        get() = when (val smbAuth = auth) {
            Server.Smb.Auth.Guest -> ""
            is Server.Smb.Auth.UsernamePassword -> smbAuth.username
        }

    private val Server.Smb.password
        get() = when (val smbAuth = auth) {
            Server.Smb.Auth.Guest -> ""
            is Server.Smb.Auth.UsernamePassword -> smbAuth.password
        }
}

package com.sorrowblue.comicviewer.server.management.smb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Smb
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerError
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.server.management.util.DomainNameTextValidator
import com.sorrowblue.comicviewer.server.management.util.HostNameTextValidator
import com.sorrowblue.comicviewer.server.management.util.PortTextValidator
import com.sorrowblue.comicviewer.server.management.util.RequireValidator
import com.sorrowblue.comicviewer.server.management.util.isErrorFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat


@HiltViewModel
internal class ServerManagementSmbViewModel @Inject constructor(
    private val registerServerUseCase: RegisterServerUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerManagementSmbFragmentArgs by navArgs()
    private val smb: Smb? = args.serverSmb
    private val folder: Folder? = args.folder

    val isAdd: Boolean = args.serverSmb == null

    val transitionName = args.transitionName

    val hostFlow = MutableStateFlow(smb?.host.orEmpty())
    val portFlow = MutableStateFlow(smb?.port ?: 445)
    val pathFlow = MutableStateFlow(folder?.path.orEmpty().removePrefix("/").removeSuffix("/"))
    val displayNameFlow = MutableStateFlow(smb?.displayName.orEmpty())
    val isGuestFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val domainFlow = MutableStateFlow(smb?.domain.orEmpty())
    val usernameFlow = MutableStateFlow(smb?.username.orEmpty())
    val passwordFlow = MutableStateFlow(smb?.password.orEmpty())

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
            if (isGuestFlow.value) Smb.Auth.Guest
            else Smb.Auth.UsernamePassword(domainFlow.value, usernameFlow.value, passwordFlow.value)
        val smb = smb?.copy(
            displayName = displayNameFlow.value,
            host = hostFlow.value,
            port = port,
            auth = auth
        ) ?: Smb(
            displayName = displayNameFlow.value,
            host = hostFlow.value,
            port = port,
            auth = auth
        )
        viewModelScope.launch {
            when (val res = registerServerUseCase.execute(RegisterServerUseCase.Request(smb, path)).first()) {
                is Result.Error -> {
                    logcat(tag = this@ServerManagementSmbViewModel::class.simpleName) { "Error: ${res.error}" }
                    when (res.error) {
                        RegisterServerError.InvalidAuth -> message.emit("この設定ではアクセスできません")
                        RegisterServerError.InvalidServerInfo -> message.emit("このサイトにクセスできません(${smb.host}${path} )。")
                        RegisterServerError.InvalidPath -> message.emit("不明なエラー")
                    }
                }
                is Result.Exception -> logcat(tag = this@ServerManagementSmbViewModel::class.simpleName) { "Error: ${res.cause}" }
                is Result.Success -> {
                    logcat(tag = this@ServerManagementSmbViewModel::class.simpleName) { "Success: $smb" }
                    done.invoke(args.serverSmb == null)
                }
            }
            isConnecting.value = false
        }
    }

    val message = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)


    private val Smb.domain
        get() = when (val smbAuth = auth) {
            Smb.Auth.Guest -> ""
            is Smb.Auth.UsernamePassword -> smbAuth.domain
        }

    private val Smb.username
        get() = when (val smbAuth = auth) {
            Smb.Auth.Guest -> ""
            is Smb.Auth.UsernamePassword -> smbAuth.username
        }

    private val Smb.password
        get() = when (val smbAuth = auth) {
            Smb.Auth.Guest -> ""
            is Smb.Auth.UsernamePassword -> smbAuth.password
        }
}

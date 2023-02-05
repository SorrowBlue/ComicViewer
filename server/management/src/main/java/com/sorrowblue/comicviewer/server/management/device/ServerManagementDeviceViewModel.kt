package com.sorrowblue.comicviewer.server.management.device

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.DeviceStorage
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerError
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class ServerManagementDeviceViewModel @Inject constructor(
    private val registerServerUseCase: RegisterServerUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerManagementDeviceFragmentArgs by navArgs()
    private val deviceStorage: DeviceStorage? = args.serverDevice
    val folder: Folder? = args.folder

    val isRegister = args.serverDevice == null

    val data = MutableStateFlow(folder?.path?.toUri())
    val transitionName = args.transitionName
    val displayNameValidator = listOf(RequireValidator())

    val displayName = MutableStateFlow(deviceStorage?.displayName.orEmpty())
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
        isConnecting.value = true
        if (isError.value) {
            isConnecting.value = false
            message.tryEmit("入力してください")
            return
        }
        val library = deviceStorage?.copy(
            displayName = displayName.value,
        ) ?: DeviceStorage(displayName.value)
        viewModelScope.launch {
            when (val res = registerServerUseCase.execute(
                RegisterServerUseCase.Request(
                    library,
                    data.value?.toString().orEmpty()
                )
            ).first()) {
                is Result.Error -> when (res.error) {
                    RegisterServerError.InvalidAuth -> message.emit("アクセス権限がありません。")
                    RegisterServerError.InvalidPath -> message.emit("フォルダが存在しません。")
                    RegisterServerError.InvalidServerInfo -> Unit
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

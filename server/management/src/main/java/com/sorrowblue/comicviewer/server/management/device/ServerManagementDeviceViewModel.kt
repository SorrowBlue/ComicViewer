package com.sorrowblue.comicviewer.server.management.device

import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.RegisterServerRequest
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryUseCase
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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class ServerManagementDeviceViewModel @Inject constructor(
    private val registerLibraryUseCase: RegisterLibraryUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerManagementDeviceFragmentArgs by navArgs()
    val deviceStorage: Server.DeviceStorage? = args.serverDevice
    val bookshelf: Bookshelf? = args.bookshelf

    val isRegister = args.serverDevice == null

    val data = MutableStateFlow(bookshelf?.path?.toUri())
    val transitionName = args.transitionName
    val displayNameValidator = listOf(RequireValidator())

    val displayName = MutableStateFlow(deviceStorage?.displayName.orEmpty())
    val dir: StateFlow<String> = data.mapNotNull { it?.lastPathSegment?.removePrefix("primary:") }
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
        ) ?: Server.DeviceStorage(displayName.value)
        viewModelScope.launch {
            when (val res = registerLibraryUseCase.execute(
                RegisterServerRequest(
                    library,
                    data.value?.toString().orEmpty()
                )
            )) {
                is Result.Error -> logcat { "Error: ${res.error}" }
                is Result.Exception -> logcat { "Error: ${res.cause}" }
                is Result.Success -> {
                    function.invoke()
                }
            }
            isConnecting.value = false
        }
    }

    val message = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)
}

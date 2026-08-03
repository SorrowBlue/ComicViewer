/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import logcat.logcat

sealed interface BookshelfEditViewModelEvent {
    data class RegisterError(val error: RegisterBookshelfUseCase.Error) :
        BookshelfEditViewModelEvent

    data object Complete : BookshelfEditViewModelEvent
}

@AssistedInject
internal class BookshelfEditViewModel(
    @Assisted private val editType: BookshelfEditType,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
) : ViewModel() {

    val event: SharedFlow<BookshelfEditViewModelEvent>
        field = MutableSharedFlow()

    val formFlow =
        when (editType) {
            is BookshelfEditType.Register -> {
                flowOf(null)
            }

            is BookshelfEditType.Edit -> {
                getBookshelfInfoUseCase(
                    GetBookshelfInfoUseCase.Request(editType.bookshelfId),
                ).mapNotNull { it.dataOrNull() }
                    .map {
                        when (val bookshelf = it.bookshelf) {
                            is DeviceStorage -> {
                                return@map DeviceEditForm(
                                    displayName = bookshelf.displayName,
                                    path = it.folder.path,
                                )
                            }

                            is SmbServer -> {
                                return@map SmbEditForm(
                                    displayName = bookshelf.displayName,
                                    host = bookshelf.host,
                                    port = bookshelf.port,
                                    path = it.folder.path
                                        .removePrefix("/")
                                        .removeSuffix("/"),
                                    auth = when (bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> SmbEditForm.Auth.Guest

                                        is SmbServer.Auth.UsernamePassword ->
                                            SmbEditForm.Auth.UserPass
                                    },
                                    domain = when (val auth = bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> ""
                                        is SmbServer.Auth.UsernamePassword -> auth.domain
                                    },
                                    username = when (val auth = bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> ""
                                        is SmbServer.Auth.UsernamePassword -> auth.username
                                    },
                                    password = when (val auth = bookshelf.auth) {
                                        is SmbServer.Auth.Guest -> ""
                                        is SmbServer.Auth.UsernamePassword -> auth.password
                                    },
                                )
                            }

                            ShareContents -> {
                                return@map null
                            }
                        }
                    }
            }
        }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun submit(form: BookshelfEditForm) {
        viewModelScope.launch {
            logcat { "#onSubmit form: $form" }
            val bookshelf: Bookshelf
            val path: String
            when (form) {
                is DeviceEditForm -> when (editType) {
                    is BookshelfEditType.Edit -> {
                        bookshelf = (getBookshelf(editType.bookshelfId) as DeviceStorage)
                            .copy(displayName = form.displayName)
                        path = requireNotNull(form.path)
                    }

                    is BookshelfEditType.Register -> {
                        bookshelf = DeviceStorage(form.displayName)
                        path = requireNotNull(form.path)
                    }
                }

                is SmbEditForm -> {
                    val paths = "/${form.path}/".replace("(/+)".toRegex(), "/")
                    val auth = when (form.auth) {
                        SmbEditForm.Auth.Guest -> SmbServer.Auth.Guest

                        SmbEditForm.Auth.UserPass -> SmbServer.Auth.UsernamePassword(
                            domain = form.domain,
                            username = form.username,
                            password = form.password,
                        )
                    }
                    when (editType) {
                        is BookshelfEditType.Edit -> {
                            bookshelf = (getBookshelf(editType.bookshelfId) as SmbServer)
                                .copy(
                                    displayName = form.displayName,
                                    host = form.host,
                                    port = form.port,
                                    auth = auth,
                                )
                            path = paths
                        }

                        is BookshelfEditType.Register -> {
                            bookshelf = SmbServer(
                                displayName = form.displayName,
                                host = form.host,
                                port = form.port,
                                auth = auth,
                            )
                            path = paths
                        }
                    }
                }
            }
            delay(300.milliseconds)
            registerBookshelfUseCase(RegisterBookshelfUseCase.Request(bookshelf, path)).fold(
                onSuccess = { event.emit(BookshelfEditViewModelEvent.Complete) },
                onError = { event.emit(BookshelfEditViewModelEvent.RegisterError(it)) },
            )
        }
    }

    private suspend fun getBookshelf(id: BookshelfId): Bookshelf = requireNotNull(
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(id))
            .first()
            .dataOrNull(),
    ).bookshelf

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(editType: BookshelfEditType): BookshelfEditViewModel
    }
}

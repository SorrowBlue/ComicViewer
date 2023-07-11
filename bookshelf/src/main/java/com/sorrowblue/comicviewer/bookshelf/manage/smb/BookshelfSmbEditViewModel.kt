package com.sorrowblue.comicviewer.bookshelf.manage.smb

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal open class BookshelfSmbEditViewModel @Inject constructor(
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfManageSmbFragmentArgs by navArgs()

    val host = MutableStateFlow("")
    val isInvalidHost = host.map { it.isEmpty() }
    val port = MutableStateFlow("445")
    val path = MutableStateFlow("")
    val displayName = MutableStateFlow("")
    val authMode = MutableStateFlow(AuthMode.GUEST)
    val domain = MutableStateFlow("")
    val username = MutableStateFlow("")
    val password = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val data =
                getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
                    .first().dataOrNull
            if (data != null && data.bookshelf is SmbServer) {
                val bookshelf = data.bookshelf as SmbServer
                when (val auth = bookshelf.auth) {
                    SmbServer.Auth.Guest -> {
                        authMode.value = AuthMode.GUEST
                        domain.value = ""
                        username.value = ""
                        password.value = ""
                    }

                    is SmbServer.Auth.UsernamePassword -> {
                        authMode.value = AuthMode.USERPASS
                        domain.value = auth.domain
                        username.value = auth.username
                        password.value = auth.password
                    }
                }
                host.value = bookshelf.host
                port.value = bookshelf.port.toString()
                path.value = data.folder.path
                displayName.value = bookshelf.displayName
            }
        }
    }

}

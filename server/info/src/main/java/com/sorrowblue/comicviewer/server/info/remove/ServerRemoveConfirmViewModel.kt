package com.sorrowblue.comicviewer.server.info.remove

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryRequest
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@HiltViewModel
internal class ServerRemoveConfirmViewModel @Inject constructor(
    private val removeLibraryUseCase: RemoveLibraryUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerRemoveConfirmDialogArgs by navArgs()

    fun remove() {
        runBlocking {
            withContext(Dispatchers.IO) {
                removeLibraryUseCase.execute(RemoveLibraryRequest(args.server))
            }
        }
    }
}

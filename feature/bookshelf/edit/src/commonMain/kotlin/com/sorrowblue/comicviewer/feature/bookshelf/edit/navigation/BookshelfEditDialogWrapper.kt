package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.DestinationScope
import com.sorrowblue.cmpdestinations.DestinationWrapper
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditDialog
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditDialogState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.rememberBookshelfEditDialogState
import org.koin.compose.module.rememberKoinModules
import org.koin.dsl.bind
import org.koin.dsl.module

object BookshelfEditDialogWrapper : DestinationWrapper {
    @Composable
    override fun DestinationScope.Content(content: @Composable (() -> Unit)) {
        val state = rememberBookshelfEditDialogState()
        @Suppress("OPT_IN_USAGE")
        (
            rememberKoinModules(unloadModules = true) {
                listOf(module { single { state } bind BookshelfEditDialogState::class })
            }
            )
        content()
        BookshelfEditDialog(state = state)
    }
}

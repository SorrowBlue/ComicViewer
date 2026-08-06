/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_device
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_smb
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_edit_of
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register_of
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import logcat.logcat
import org.jetbrains.compose.resources.getString

internal interface BookshelfEditScreenState {
    fun onSourceClick(type: BookshelfType)
    fun updateCanSubmit(value: Boolean)
    fun onBack(): Boolean
    fun discardConfirm(force: Boolean)

    val uiState: BookshelfEditScreenUiState
    val backStack: NavBackStack<NavKey>
}

@Composable
internal fun rememberBookshelfEditScreenState(
    key: BookshelfWizardNavKey,
): BookshelfEditScreenState {
    val coroutineScope = rememberCoroutineScope()
    val initialKey = when (key) {
        BookshelfWizardNavKey.Selection -> BookshelfEditPage.WizardSelection

        is BookshelfWizardNavKey.Edit ->
            BookshelfEditPage.WizardEdit(BookshelfEditType.Edit(key.bookshelfId, key.bookshelfType))
    }
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                contextual(SnapshotStateListSerializer(PolymorphicSerializer(NavKey::class)))
                polymorphic(NavKey::class) {
                    subclass(BookshelfEditPage.WizardSelection.serializer())
                    subclass(BookshelfEditPage.WizardEdit.serializer())
                    subclass(BookshelfEditPage.Discard.serializer())
                }
            }
        },
        initialKey,
    )
    val state = remember(key, backStack, coroutineScope) {
        BookshelfEditScreenStateImpl(key, backStack, coroutineScope)
    }
    return state
}

private class BookshelfEditScreenStateImpl(
    key: BookshelfWizardNavKey,
    override val backStack: NavBackStack<NavKey>,
    private val coroutineScope: CoroutineScope,
) : BookshelfEditScreenState {

    override var uiState by mutableStateOf(BookshelfEditScreenUiState())

    init {
        when (key) {
            BookshelfWizardNavKey.Selection -> {
                updateTitle {
                    getString(Res.string.bookshelf_wizard_title_register)
                }
            }

            is BookshelfWizardNavKey.Edit -> {
                updateTitle {
                    getString(
                        Res.string.bookshelf_wizard_title_edit_of,
                        key.bookshelfType.displayName(),
                    )
                }
            }
        }
    }

    override fun onBack(): Boolean {
        logcat { "#onBack backStack.size: ${backStack.size}" }
        if (1 < backStack.size) {
            backStack.removeLast()
            return true
        } else {
            return false
        }
    }

    override fun discardConfirm(force: Boolean) {
        logcat { "#discardConfirm force: $force backStack: ${backStack.toList()}" }
        backStack.add(BookshelfEditPage.Discard(force))
    }

    override fun updateCanSubmit(value: Boolean) {
        uiState = uiState.copy(canSubmit = value)
    }

    override fun onSourceClick(type: BookshelfType) {
        val editPage = when (type) {
            BookshelfType.SMB -> BookshelfEditPage.WizardEdit(
                BookshelfEditType.Register(
                    BookshelfType.SMB,
                ),
            )

            BookshelfType.DEVICE -> BookshelfEditPage.WizardEdit(
                BookshelfEditType.Register(
                    BookshelfType.DEVICE,
                ),
            )
        }
        if (backStack.lastOrNull() is BookshelfEditPage.WizardEdit) {
            backStack.removeAt(backStack.lastIndex)
        }
        backStack.add(editPage)
        updateTitle {
            getString(
                Res.string.bookshelf_wizard_title_register_of,
                type.displayName(),
            )
        }
    }

    private fun updateTitle(update: suspend () -> String) {
        coroutineScope.launch {
            uiState = uiState.copy(title = update())
        }
    }
}

suspend fun BookshelfType.displayName() = when (this) {
    BookshelfType.SMB -> getString(Res.string.bookshelf_edit_selection_label_smb)
    BookshelfType.DEVICE -> getString(Res.string.bookshelf_edit_selection_label_device)
}

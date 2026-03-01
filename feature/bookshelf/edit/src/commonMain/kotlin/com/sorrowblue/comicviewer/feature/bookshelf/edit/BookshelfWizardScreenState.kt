package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.framework.ui.material3.animateScrollToPage
import com.sorrowblue.comicviewer.framework.ui.saveable.rememberListSaveable
import comicviewer.feature.bookshelf.edit.generated.resources.Res
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_device
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_edit_selection_label_smb
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_edit_of
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register
import comicviewer.feature.bookshelf.edit.generated.resources.bookshelf_wizard_title_register_of
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.getString

@Serializable
sealed interface BookshelfWizardPage {

    @Serializable
    data object Selection : BookshelfWizardPage

    @Serializable
    data class Edit(val editType: BookshelfEditType) : BookshelfWizardPage
}

internal interface BookshelfWizardScreenState {
    fun onSourceClick(type: BookshelfType)
    fun onBack(): Boolean
    fun onNextClick()
    fun onPrevClick()
    fun onKeep()
    fun onFormChange(isSame: Boolean)

    val uiState: BookshelfWizardScreenUiState
    var showDiscardDialog: Boolean
    val pagerState: PagerState
    val pages: SnapshotStateList<BookshelfWizardPage>
}

@Composable
internal fun rememberBookshelfWizardScreenState(
    key: BookshelfWizardNavKey,
): BookshelfWizardScreenState {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberListSaveable(key, save = {
        val currentPage = it.pagerState.currentPage
        val pages = Json.encodeToString(it.pages.toList())
        listOf(currentPage, pages)
    }, restore = {
        val currentPage = it[0] as Int
        val pages = Json.decodeFromString<List<BookshelfWizardPage>>(it[1] as String)
        this.pages.clear()
        this.pages.addAll(pages)
        this.pagerState.requestScrollToPage(currentPage)
    }) {
        BookshelfWizardScreenStateImpl(key, coroutineScope)
    }
    return state
}

private class BookshelfWizardScreenStateImpl(
    key: BookshelfWizardNavKey,
    private val coroutineScope: CoroutineScope,
) : BookshelfWizardScreenState {

    override val pages = mutableStateListOf<BookshelfWizardPage>()

    override var uiState by mutableStateOf(BookshelfWizardScreenUiState())

    override var showDiscardDialog by mutableStateOf(false)

    override val pagerState = PagerState { pages.size }

    private var isSameForm = true

    override fun onFormChange(isSame: Boolean) {
        isSameForm = isSame
    }

    override fun onNextClick() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(true)
        }
    }

    override fun onPrevClick() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(false)
        }
    }

    private fun updateTitle(update: suspend () -> String) {
        coroutineScope.launch {
            uiState = uiState.copy(title = update())
        }
    }

    init {
        when (key) {
            BookshelfWizardNavKey.Selection -> {
                pages.add(BookshelfWizardPage.Selection)
                updateTitle {
                    getString(Res.string.bookshelf_wizard_title_register)
                }
            }

            is BookshelfWizardNavKey.Edit -> {
                pages.add(
                    BookshelfWizardPage.Edit(
                        BookshelfEditType.Edit(
                            key.bookshelfId,
                            key.bookshelfType,
                        ),
                    ),
                )
                updateTitle {
                    getString(
                        Res.string.bookshelf_wizard_title_edit_of,
                        key.bookshelfType.displayName(),
                    )
                }
            }
        }
    }

    override fun onKeep() {
        showDiscardDialog = false
    }

    override fun onBack(): Boolean {
        if (!isSameForm) {
            showDiscardDialog = true
            return true
        } else if (0 < pagerState.currentPage) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            }
            return true
        } else {
            return false
        }
    }

    override fun onSourceClick(type: BookshelfType) {
        val device = BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.DEVICE))
        val smb = BookshelfWizardPage.Edit(BookshelfEditType.Register(BookshelfType.SMB))
        when (type) {
            BookshelfType.SMB -> {
                if (pages.contains(smb)) {
                    // Do nothing
                } else if (pages.contains(device)) {
                    pages.removeAt(pages.lastIndex)
                    pages.add(smb)
                } else {
                    pages.add(smb)
                }
                updateTitle {
                    getString(
                        Res.string.bookshelf_wizard_title_register_of,
                        BookshelfType.SMB.displayName(),
                    )
                }
                coroutineScope.launch {
                    delay(50)
                    pagerState.animateScrollToPage(pages.lastIndex)
                }
            }

            BookshelfType.DEVICE -> {
                if (pages.contains(device)) {
                    // Do nothing
                } else if (pages.contains(smb)) {
                    pages.removeAt(pages.lastIndex)
                    pages.add(device)
                } else {
                    pages.add(device)
                }
                updateTitle {
                    getString(
                        Res.string.bookshelf_wizard_title_register_of,
                        BookshelfType.DEVICE.displayName(),
                    )
                }
                coroutineScope.launch {
                    delay(50)
                    pagerState.animateScrollToPage(pages.lastIndex)
                }
            }
        }
    }
}

suspend fun BookshelfType.displayName() = when (this) {
    BookshelfType.SMB -> getString(Res.string.bookshelf_edit_selection_label_smb)
    BookshelfType.DEVICE -> getString(Res.string.bookshelf_edit_selection_label_device)
}

package com.sorrowblue.comicviewer.bookshelf.management.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookshelfManagementSelectionViewModel @Inject constructor(
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs

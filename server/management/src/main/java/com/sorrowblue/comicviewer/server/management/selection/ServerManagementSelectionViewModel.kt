package com.sorrowblue.comicviewer.server.management.selection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ServerManagementSelectionViewModel @Inject constructor(
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {
    private val args: ServerManagementSelectionFragmentArgs by navArgs()

    val transitionName = args.transitionName
}

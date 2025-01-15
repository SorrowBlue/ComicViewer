package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual inline fun <reified VM : ViewModel> viewModels(
    viewModelStoreOwner: ViewModelStoreOwner,
    key: String?,
): VM {
    return koinViewModel(viewModelStoreOwner = viewModelStoreOwner, key = key)
}

package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner

@Composable
actual inline fun <reified VM : ViewModel> viewModels(
    viewModelStoreOwner: ViewModelStoreOwner,
    key: String?,
): VM {
    return hiltViewModel(viewModelStoreOwner, key)
}

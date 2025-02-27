package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.ui.platform.findComposeDefaultViewModelStoreOwner
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(InternalComposeApi::class)
@Composable
actual inline fun <reified T : ViewModel> sharedKoinViewModel(): T {
    val owner = findComposeDefaultViewModelStoreOwner()!!
    return koinViewModel(viewModelStoreOwner = owner)
}

package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner

@Composable
actual inline fun <reified T : ViewModel> sharedKoinViewModel(): T {
    TODO("Not yet implemented")
}

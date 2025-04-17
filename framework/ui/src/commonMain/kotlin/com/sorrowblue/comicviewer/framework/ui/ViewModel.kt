package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

@Composable
expect inline fun <reified T : ViewModel> sharedKoinViewModel(): T

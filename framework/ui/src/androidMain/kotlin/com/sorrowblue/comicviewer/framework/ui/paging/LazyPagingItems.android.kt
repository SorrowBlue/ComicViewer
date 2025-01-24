package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.ui.platform.AndroidUiDispatcher
import kotlin.coroutines.CoroutineContext

internal actual val mainDispatcher: CoroutineContext get() = AndroidUiDispatcher.Main

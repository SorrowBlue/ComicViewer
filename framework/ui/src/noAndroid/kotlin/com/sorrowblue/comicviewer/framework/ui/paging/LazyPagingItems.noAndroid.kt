package com.sorrowblue.comicviewer.framework.ui.paging

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

internal actual val mainDispatcher: CoroutineContext get() = Dispatchers.Main

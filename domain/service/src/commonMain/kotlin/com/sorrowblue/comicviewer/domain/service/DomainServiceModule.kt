package com.sorrowblue.comicviewer.domain.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Single

@IoDispatcher
@Single
fun provideIoDispatcherForKmp(): CoroutineDispatcher = Dispatchers.IO

package com.sorrowblue.comicviewer.domain.service

import di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.Singleton

@IoDispatcher
@Singleton
fun provideIoDispatcherForKmp(): CoroutineDispatcher = Dispatchers.IO

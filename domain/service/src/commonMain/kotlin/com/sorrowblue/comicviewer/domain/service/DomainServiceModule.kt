package com.sorrowblue.comicviewer.domain.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import jakarta.inject.Singleton

@IoDispatcher
@Singleton
fun provideIoDispatcherForKmp(): CoroutineDispatcher = Dispatchers.IO

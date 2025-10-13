package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import jakarta.inject.Singleton

@ImageExtension
@Singleton
fun bindSupportedImage(): Set<String> = SUPPORTED_IMAGE

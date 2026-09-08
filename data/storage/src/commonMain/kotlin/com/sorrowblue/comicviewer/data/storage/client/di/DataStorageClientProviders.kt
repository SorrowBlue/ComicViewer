/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.model.file.SUPPORTED_IMAGE
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
object DataStorageClientProviders {
    @Provides
    @ImageExtension
    fun bindSupportedImage(): Set<String> = SUPPORTED_IMAGE
}

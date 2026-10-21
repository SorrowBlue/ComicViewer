/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.fetcher

import coil3.decode.ImageSource
import coil3.decode.ImageSource.Metadata
import coil3.disk.DiskCache
import kotlinx.io.Source
import kotlinx.io.okio.asOkioSource
import okio.FileSystem as OkioFileSystem
import okio.Path
import okio.SYSTEM
import okio.buffer

internal fun ImageSource(source: Source, metadata: Metadata? = null): ImageSource =
    ImageSource(source.asOkioSource().buffer(), OkioFileSystem.SYSTEM, metadata)

internal fun DiskCache.Snapshot.toImageSource(diskCacheKey: String? = null): ImageSource =
    ImageSource(
        file = data,
        fileSystem = OkioFileSystem.SYSTEM,
        diskCacheKey = diskCacheKey,
        closeable = this,
    )

internal fun Path.asKotlinxPath() = kotlinx.io.files.Path(toString())

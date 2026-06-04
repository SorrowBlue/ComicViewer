package com.sorrowblue.comicviewer.data.coil.fetcher

import coil3.decode.ImageSource
import coil3.decode.ImageSource.Metadata
import kotlinx.io.Source
import kotlinx.io.files.FileSystem
import kotlinx.io.okio.asOkioSource
import okio.Path
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.buffer

internal fun ImageSource(
    source: Source,
    fileSystem: FileSystem,
    metadata: Metadata? = null,
): ImageSource =
    ImageSource(source.asOkioSource().buffer(), fileSystem.asOkioFileSystem(), metadata)

internal fun ImageSource(
    file: kotlinx.io.files.Path,
    fileSystem: FileSystem,
    diskCacheKey: String? = null,
    closeable: AutoCloseable? = null,
    metadata: Metadata? = null,
): ImageSource =
    ImageSource(file.asOkioPath(), fileSystem.asOkioFileSystem(), diskCacheKey, closeable, metadata)

internal fun FileSystem.asOkioFileSystem() = okio.FileSystem.SYSTEM

internal fun kotlinx.io.files.Path.asOkioPath() = toString().toPath()

internal fun Path.asKotlinxPath() = kotlinx.io.files.Path(toString())

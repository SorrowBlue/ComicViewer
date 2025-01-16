package com.sorrowblue.comicviewer.data.storage.client.qualifier

import org.koin.core.annotation.Named

// TODO Rename -> ImageExtension
@Named
@Retention(AnnotationRetention.BINARY)
annotation class ImageExtension2

@Named
@Retention(AnnotationRetention.BINARY)
annotation class ZipFileReader

@Named
@Retention(AnnotationRetention.BINARY)
annotation class SmbFileClient

@Named
@Retention(AnnotationRetention.BINARY)
annotation class DeviceFileClient

@Named
@Retention(AnnotationRetention.BINARY)
annotation class ShareFileClient

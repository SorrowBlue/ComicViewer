package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.data.storage.client.impl.ImageFolderFileReader
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteException
import dev.zacsweers.metro.DefaultBinding
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.MapKey
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import logcat.asLog
import logcat.logcat
import okio.BufferedSource

@MapKey
annotation class FileClientKey(val value: KClass<out Bookshelf>)

typealias FileClientFactory = Map<KClass<out Bookshelf>, FileClient.Factory<*>>

@Suppress("UNCHECKED_CAST")
fun FileClientFactory.fileClient(
    bookshelf: Bookshelf,
): ReadOnlyProperty<Any, FileClient<out Bookshelf>> =
    ReadOnlyProperty<Any, FileClient<out Bookshelf>> { thisRef, property ->
        val factory = when (bookshelf) {
            is DeviceStorage -> getValue(DeviceStorage::class)
            is SmbServer -> getValue(SmbServer::class)
            ShareContents -> getValue(ShareContents::class)
        } as FileClient.Factory<Bookshelf>
        factory.create(bookshelf)
    }

@Suppress("UNCHECKED_CAST")
fun FileClientFactory.getFileClient(bookshelf: Bookshelf): FileClient<Bookshelf> {
    val factory = when (bookshelf) {
        is DeviceStorage -> getValue(DeviceStorage::class)
        is SmbServer -> getValue(SmbServer::class)
        ShareContents -> getValue(ShareContents::class)
    } as FileClient.Factory<Bookshelf>
    return factory.create(bookshelf)
}

abstract class FileClient<T : Bookshelf>(
    val bookshelf: T,
    val fileReaderFactoryMap: Map<FileReaderType, FileReaderFactory>,
    val dispatcher: CoroutineDispatcher,
) {
    @Suppress("RemoveRedundantQualifierName")
    @OptIn(ExperimentalMetroApi::class)
    @DefaultBinding<FileClient.Factory<*>>()
    fun interface Factory<T : Bookshelf> {
        fun create(bookshelf: T): FileClient<T>
    }

    abstract suspend fun listFiles(file: File, resolveImageFolder: Boolean = false): List<File>

    abstract suspend fun exists(path: String): Boolean

    abstract suspend fun current(path: String, resolveImageFolder: Boolean = false): File

    abstract suspend fun bufferedSource(file: File): BufferedSource

    abstract suspend fun seekableInputStream(file: File): SeekableInputStream

    abstract suspend fun connect(path: String)

    abstract suspend fun attribute(path: String): FileAttribute

    abstract suspend fun fileSize(path: String): Long

    suspend fun fileReader(book: Book): FileReader = withContext(dispatcher) {
        runCatching {
            when (book) {
                is BookFile -> {
                    val seekableInputStream = seekableInputStream(book)
                    when (book.extension) {
                        "pdf", "epub", "xps", "oxps", "mobi", "fb2" ->
                            fileReaderFactoryMap.getValue(FileReaderType.Document)

                        else -> fileReaderFactoryMap.getValue(FileReaderType.Zip)
                    }.create(book.extension, seekableInputStream)
                }

                is BookFolder -> ImageFolderFileReader(dispatcher, this@FileClient, book)
            }
        }.getOrElse {
            logcat { it.asLog() }
            throw when (it) {
                is FileClientException -> when (it) {
                    is FileClientException.InvalidAuth -> RemoteException.InvalidAuth()
                    is FileClientException.InvalidPath -> RemoteException.NotFound()
                    is FileClientException.InvalidServer -> RemoteException.InvalidServer()
                    is FileClientException.NoNetwork -> RemoteException.NoNetwork()
                }

                is FileReaderException -> when (it) {
                    is FileReaderException.NotSupport -> RemoteException.NotFound()
                }

                else -> it
            }
        }
    }
}

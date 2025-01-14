package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path

internal fun <T> createDataStore(
    fileSystem: FileSystem,
    serializer: OkioSerializer<T>,
    producePath: () -> Path,
): DataStore<T> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = serializer,
        ),
    )

package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer

internal expect class DataStoreMaker {
    fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T>
}

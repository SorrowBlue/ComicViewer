package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import org.koin.core.annotation.Single

@Single
internal expect class DataStoreMaker(context: PlatformContext) {
    fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T>
}

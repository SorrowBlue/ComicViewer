package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.domain.service.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher

@Inject
internal expect class DataStoreMaker(
    context: PlatformContext,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) {
    fun <T> createDataStore(okioSerializer: OkioKSerializer<T>): DataStore<T>
}

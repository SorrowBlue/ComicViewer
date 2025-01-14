package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.KOkioSerializer

internal expect fun <T : Any> createDataStore(serializer: KOkioSerializer<T>): DataStore<T>

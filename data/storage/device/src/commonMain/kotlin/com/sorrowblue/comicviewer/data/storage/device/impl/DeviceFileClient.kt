package com.sorrowblue.comicviewer.data.storage.device.impl

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage

internal expect class DeviceFileClient : FileClient<InternalStorage>

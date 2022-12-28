package com.sorrowblue.comicviewer.data.remote.client

import com.sorrowblue.comicviewer.data.common.ServerModel

interface FileClientFactory {

    fun create(serverModel: ServerModel): FileClient
}

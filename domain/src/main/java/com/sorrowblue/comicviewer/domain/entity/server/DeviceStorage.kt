package com.sorrowblue.comicviewer.domain.entity.server

data class DeviceStorage(
    override val id: ServerId,
    override val displayName: String
) : Server {
    constructor(displayName: String) : this(ServerId(0), displayName)
}

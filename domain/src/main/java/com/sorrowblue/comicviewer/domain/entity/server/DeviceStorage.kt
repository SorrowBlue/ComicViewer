package com.sorrowblue.comicviewer.domain.entity.server

import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceStorage(
    override val id: ServerId,
    override val displayName: String
) : Server {
    constructor(displayName: String) : this(ServerId(0), displayName)
}

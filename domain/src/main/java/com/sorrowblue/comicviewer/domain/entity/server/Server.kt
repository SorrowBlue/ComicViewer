package com.sorrowblue.comicviewer.domain.entity.server

sealed interface Server {
    val id: ServerId
    val displayName: String
}

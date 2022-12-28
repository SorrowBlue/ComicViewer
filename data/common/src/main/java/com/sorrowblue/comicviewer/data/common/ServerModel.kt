package com.sorrowblue.comicviewer.data.common

sealed interface ServerModel {

    val id: ServerModelId
    val name: String
}

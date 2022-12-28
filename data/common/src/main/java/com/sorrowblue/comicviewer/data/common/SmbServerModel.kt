package com.sorrowblue.comicviewer.data.common

data class SmbServerModel(
    override val id: ServerModelId,
    override val name: String,
    val host: String,
    val port: String,
    val auth: Auth
) : ServerModel {

    sealed interface Auth

    object Guest : Auth
    data class UsernamePassword(val username: String, val password: String) : Auth
}

package com.sorrowblue.comicviewer.domain.entity.server

data class Smb(
    override val id: ServerId,
    override val displayName: String,
    val host: String,
    val port: Int,
    val auth: Auth
) : Server {

    constructor(displayName: String, host: String, port: Int, auth: Auth) : this(
        ServerId(0),
        displayName,
        host,
        port,
        auth
    )

    sealed interface Auth {
        data class UsernamePassword(
            val domain: String,
            val username: String,
            val password: String
        ) : Auth

        object Guest : Auth
    }
}

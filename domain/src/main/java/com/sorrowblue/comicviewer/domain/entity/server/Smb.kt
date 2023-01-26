package com.sorrowblue.comicviewer.domain.entity.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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

    sealed interface Auth : Parcelable {
        @Parcelize
        data class UsernamePassword(
            val domain: String,
            val username: String,
            val password: String
        ) : Auth

        @Parcelize
        object Guest : Auth
    }
}

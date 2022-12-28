package com.sorrowblue.comicviewer.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Server : Parcelable {
    val id: ServerId
    val displayName: String

    @Parcelize
    data class Smb(
        override val id: ServerId,
        override val displayName: String,
        val host: String,
        val port: String,
        val auth: Auth
    ) : Server {

        constructor(displayName: String, host: String, port: String, auth: Auth) : this(
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

    @Parcelize
    data class DeviceStorage(
        override val id: ServerId,
        override val displayName: String
    ) : Server {
        constructor(displayName: String) : this(ServerId(0), displayName)
    }
}

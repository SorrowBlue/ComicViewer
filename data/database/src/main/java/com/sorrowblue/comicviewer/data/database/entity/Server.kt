package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SmbServerModel

@Entity(tableName = "server")
internal data class Server(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "display_name") val displayName: String,
    val type: Type,
    /*↓SmbServerModel↓*/
    val host: String,
    val port: Int,
    val domain: String,
    val username: String,
    val password: DecryptedPassword
) {

    enum class Type {
        SMB, DEVICE
    }

    fun toServerModel(): ServerModel = when (type) {
        Type.SMB -> SmbServerModel(
            ServerModelId(id),
            displayName,
            host,
            port,
            if (username.isEmpty()) SmbServerModel.Guest
            else SmbServerModel.UsernamePassword(domain, username, password.plane)
        )
        Type.DEVICE -> DeviceStorageModel(ServerModelId(id), displayName)
    }
}

internal fun ServerModel.toServer() = when (this) {
    is DeviceStorageModel -> Server(
        id.value,
        name,
        Server.Type.DEVICE,
        "",
        0,
        "",
        "",
        DecryptedPassword("")
    )
    is SmbServerModel -> Server(
        id.value,
        name,
        Server.Type.SMB,
        host,
        port,
        auth.domain,
        auth.username,
        auth.password
    )
}

private val SmbServerModel.Auth.domain
    get() = when (this) {
        SmbServerModel.Guest -> ""
        is SmbServerModel.UsernamePassword -> domain
    }
private val SmbServerModel.Auth.username
    get() = when (this) {
        SmbServerModel.Guest -> ""
        is SmbServerModel.UsernamePassword -> username
    }
private val SmbServerModel.Auth.password
    get() = when (this) {
        SmbServerModel.Guest -> DecryptedPassword("password")
        is SmbServerModel.UsernamePassword -> DecryptedPassword(password)
    }


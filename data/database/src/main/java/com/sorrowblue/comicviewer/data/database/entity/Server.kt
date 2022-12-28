package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.SmbServerModel

@Entity(tableName = "server")
internal data class Server(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "display_name") val displayName: String,
    val type: Type,
    /*↓SmbServerModel↓*/
    val host: String,
    val port: String,
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
            else SmbServerModel.UsernamePassword(username, password.plane)
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
        auth.username,
        auth.password
    )
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

internal class ServerFile(
    @Embedded val server: Server,
    @Embedded val file: File
)

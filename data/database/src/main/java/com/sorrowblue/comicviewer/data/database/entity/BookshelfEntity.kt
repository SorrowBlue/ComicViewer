package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer

@Entity(tableName = "bookshelf")
internal data class BookshelfEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID) val id: Int,
    @ColumnInfo("display_name") val displayName: String,
    val type: Type,
    /*↓SmbServer↓*/
    val host: String,
    val port: Int,
    val domain: String,
    val username: String,
    val password: DecryptedPasswordEntity,
) {

    companion object {
        const val ID = "id"

        fun fromModel(model: Bookshelf) = when (model) {
            is InternalStorage -> BookshelfEntity(
                id = model.id.value,
                displayName = model.displayName,
                type = Type.INTERNAL,
                host = "",
                port = 0,
                domain = "",
                username = "",
                password = DecryptedPasswordEntity("")
            )

            is SmbServer -> BookshelfEntity(
                id = model.id.value,
                displayName = model.displayName,
                type = Type.SMB,
                host = model.host,
                port = model.port,
                domain = when (val auth = model.auth) {
                    SmbServer.Auth.Guest -> ""
                    is SmbServer.Auth.UsernamePassword -> auth.domain
                },
                username = when (val auth = model.auth) {
                    SmbServer.Auth.Guest -> ""
                    is SmbServer.Auth.UsernamePassword -> auth.username
                },
                password = when (val auth = model.auth) {
                    SmbServer.Auth.Guest -> DecryptedPasswordEntity("")
                    is SmbServer.Auth.UsernamePassword -> DecryptedPasswordEntity(auth.password)
                }
            )
        }
    }

    enum class Type { INTERNAL, SMB }

    fun toModel(fileCount: Int): Bookshelf = when (type) {
        Type.INTERNAL -> InternalStorage(
            id = BookshelfId(id),
            displayName = displayName,
            fileCount = fileCount
        )

        Type.SMB -> SmbServer(
            id = BookshelfId(id),
            displayName = displayName,
            host = host,
            port = port,
            auth = if (username.isEmpty()) {
                SmbServer.Auth.Guest
            } else {
                SmbServer.Auth.UsernamePassword(domain, username, password.plane)
            },
            fileCount = fileCount
        )
    }
}

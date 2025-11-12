package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer

@Entity(tableName = "bookshelf")
internal data class BookshelfEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID) val id: BookshelfId,
    @ColumnInfo("display_name") val displayName: String,
    val type: Type,
    @ColumnInfo(name = "deleted", defaultValue = "false") val deleted: Boolean,
    // ↓SmbServer↓
    val host: String,
    val port: Int,
    val domain: String,
    val username: String,
    val password: DecryptedPassword,
) {
    companion object {
        const val ID = "id"

        fun fromModel(model: Bookshelf) = when (model) {
            is InternalStorage -> BookshelfEntity(
                id = model.id,
                displayName = model.displayName,
                type = Type.INTERNAL,
                deleted = false,
                host = "",
                port = 0,
                domain = "",
                username = "",
                password = DecryptedPassword(""),
            )

            is SmbServer -> BookshelfEntity(
                id = model.id,
                displayName = model.displayName,
                type = Type.SMB,
                deleted = false,
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
                    SmbServer.Auth.Guest -> DecryptedPassword("")
                    is SmbServer.Auth.UsernamePassword -> DecryptedPassword(auth.password)
                },
            )

            ShareContents -> BookshelfEntity(
                id = model.id,
                displayName = model.displayName,
                type = Type.SHARE_CONTENTS,
                deleted = false,
                host = "",
                port = 0,
                domain = "",
                username = "",
                password = DecryptedPassword(""),
            )
        }
    }

    enum class Type { INTERNAL, SMB, SHARE_CONTENTS }

    fun toModel(fileCount: Int): Bookshelf = when (type) {
        Type.INTERNAL -> InternalStorage(
            id = id,
            displayName = displayName,
            fileCount = fileCount,
            isDeleted = deleted,
        )

        Type.SMB -> SmbServer(
            id = id,
            displayName = displayName,
            isDeleted = deleted,
            host = host,
            port = port,
            auth = if (username.isEmpty()) {
                SmbServer.Auth.Guest
            } else {
                SmbServer.Auth.UsernamePassword(domain, username, password.plane)
            },
            fileCount = fileCount,
        )

        Type.SHARE_CONTENTS -> ShareContents
    }
}

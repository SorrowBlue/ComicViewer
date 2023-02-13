package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId

@Entity(tableName = "bookshelf")
internal data class Bookshelf(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(ID) val id: Int,
    @ColumnInfo("display_name") val displayName: String,
    val type: Type,
    /*↓SmbServer↓*/
    val host: String,
    val port: Int,
    val domain: String,
    val username: String,
    val password: DecryptedPassword
) {

    companion object {
        const val ID = "id"

        fun fromModel(model: BookshelfModel) = when (model) {
            is BookshelfModel.InternalStorage -> Bookshelf(
                model.id.value,
                model.name,
                Type.INTERNAL,
                "",
                0,
                "",
                "",
                DecryptedPassword("")
            )

            is BookshelfModel.SmbServer -> Bookshelf(
                model.id.value,
                model.name,
                Type.SMB,
                model.host,
                model.port,
                when (val auth = model.auth) {
                    BookshelfModel.SmbServer.Guest -> ""
                    is BookshelfModel.SmbServer.UsernamePassword -> auth.domain
                },
                when (val auth = model.auth) {
                    BookshelfModel.SmbServer.Guest -> ""
                    is BookshelfModel.SmbServer.UsernamePassword -> auth.username
                },
                when (val auth = model.auth) {
                    BookshelfModel.SmbServer.Guest -> DecryptedPassword("")
                    is BookshelfModel.SmbServer.UsernamePassword -> DecryptedPassword(auth.password)
                }
            )
        }
    }

    enum class Type { INTERNAL, SMB }

    fun toModel(fileCount: Int): BookshelfModel = when (type) {
        Type.INTERNAL -> BookshelfModel.InternalStorage(
            BookshelfModelId(id),
            displayName,
            fileCount
        )

        Type.SMB -> BookshelfModel.SmbServer(
            BookshelfModelId(id),
            displayName,
            host,
            port,
            if (username.isEmpty()) {
                BookshelfModel.SmbServer.Guest
            } else {
                BookshelfModel.SmbServer.UsernamePassword(domain, username, password.plane)
            },
            fileCount
        )
    }
}

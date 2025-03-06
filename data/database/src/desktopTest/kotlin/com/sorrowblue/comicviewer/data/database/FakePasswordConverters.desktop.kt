package com.sorrowblue.comicviewer.data.database

import androidx.room.TypeConverter
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtils
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword

internal actual class FakePasswordConverters {

    @TypeConverter
    actual fun decrypt(value: String) =
        DecryptedPassword(CryptUtils.decrypt(ALIAS, value).orEmpty())

    @TypeConverter
    actual fun encrypt(value: DecryptedPassword) = CryptUtils.encrypt(ALIAS, value.plane)

    companion object {
        private const val ALIAS = "library-data.password"
    }
}

package com.sorrowblue.comicviewer.data.database

import androidx.room.TypeConverter
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword

/**
 * UnitTestでAndroidKeyStoreが使用できないので暗号化しない
 */
internal actual class FakePasswordConverters {

    @TypeConverter
    actual fun decrypt(value: String) =
        DecryptedPassword(value)

    @TypeConverter
    actual fun encrypt(value: DecryptedPassword) = value.plane
}

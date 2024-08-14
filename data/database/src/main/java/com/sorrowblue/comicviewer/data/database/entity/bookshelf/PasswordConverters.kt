package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room.TypeConverter

private const val ALIAS = "library-data.password"

internal class PasswordConverters {

    @TypeConverter
    fun decrypt(value: String): DecryptedPassword = DecryptedPassword(
        CryptUtils.decrypt(ALIAS, value).orEmpty()
    )

    @TypeConverter
    fun encrypt(value: DecryptedPassword): String = CryptUtils.encrypt(ALIAS, value.plane)
}

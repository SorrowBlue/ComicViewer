package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.TypeConverter

private const val ALIAS = "library-data.password"

internal class PasswordConverters {

    @TypeConverter
    fun decrypt(value: String): DecryptedPasswordEntity = DecryptedPasswordEntity(
        CryptUtils.decrypt(ALIAS, value).orEmpty()
    )

    @TypeConverter
    fun encrypt(value: DecryptedPasswordEntity): String = CryptUtils.encrypt(ALIAS, value.plane)
}

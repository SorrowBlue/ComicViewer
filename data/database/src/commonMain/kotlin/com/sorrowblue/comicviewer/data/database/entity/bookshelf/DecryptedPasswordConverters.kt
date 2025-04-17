package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import org.koin.core.annotation.Singleton

private const val ALIAS = "library-data.password"

@Singleton
@ProvidedTypeConverter
internal class DecryptedPasswordConverters(private val cryptUtil: CryptUtil) {

    @TypeConverter
    fun decrypt(value: String): DecryptedPassword = DecryptedPassword(
        cryptUtil.decrypt(ALIAS, value).orEmpty()
    )

    @TypeConverter
    fun encrypt(value: DecryptedPassword): String = cryptUtil.encrypt(ALIAS, value.plane)
}

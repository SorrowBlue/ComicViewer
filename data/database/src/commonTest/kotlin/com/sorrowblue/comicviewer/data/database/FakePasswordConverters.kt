package com.sorrowblue.comicviewer.data.database

import androidx.room.TypeConverter
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword

internal expect class FakePasswordConverters() {

    @TypeConverter
    fun decrypt(value: String): DecryptedPassword

    @TypeConverter
    fun encrypt(value: DecryptedPassword): String
}

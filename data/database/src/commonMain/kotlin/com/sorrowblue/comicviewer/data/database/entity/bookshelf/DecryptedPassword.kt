package com.sorrowblue.comicviewer.data.database.entity.bookshelf

import androidx.room3.ColumnTypeConverters
import kotlin.jvm.JvmInline

@ColumnTypeConverters(DecryptedPasswordConverters::class)
@JvmInline
internal value class DecryptedPassword(val plane: String)

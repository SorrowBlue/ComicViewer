package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry

internal actual inline fun <reified T : RoomDatabase> createDatabase(): T {
    val context = InstrumentationRegistry.getInstrumentation().context
    return Room.inMemoryDatabaseBuilder(context, T::class.java)
        .build()
}

internal actual fun setupComicViewerDatabaseTest() {
}

internal actual fun releaseComicViewerDatabaseTest() {
}

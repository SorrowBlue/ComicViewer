package com.sorrowblue.comicviewer.data.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry
import dev.zacsweers.metro.Inject

@Inject
internal actual class TestDatabaseHelper {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val context = InstrumentationRegistry.getInstrumentation().context
        return Room.inMemoryDatabaseBuilder<ComicViewerDatabase>(context)
    }
}

package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject

@Inject
internal actual class DatabaseHelper actual constructor(private val context: PlatformContext) {
    actual fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DatabaseName)
        return Room
            .databaseBuilder<ComicViewerDatabase>(
                context = appContext,
                name = dbFile.absolutePath,
            ).enableMultiInstanceInvalidation()
            .apply {
                val list = appContext.assets.list("database")
                if (!list.isNullOrEmpty() && list.contains("comic_viewer_database.db")) {
                    createFromAsset("database/comic_viewer_database.db")
                }
            }
    }
}

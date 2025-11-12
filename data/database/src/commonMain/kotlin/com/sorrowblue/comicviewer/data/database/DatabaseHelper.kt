package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Inject

@Inject
internal expect class DatabaseHelper(context: PlatformContext) {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}

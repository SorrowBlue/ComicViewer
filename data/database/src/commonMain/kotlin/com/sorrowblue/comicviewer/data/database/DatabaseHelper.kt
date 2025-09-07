package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import org.koin.core.annotation.Single

@Single
internal expect class DatabaseHelper(context: PlatformContext) {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}

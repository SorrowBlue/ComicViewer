package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import jakarta.inject.Singleton

@Singleton
internal expect class DatabaseHelper(context: PlatformContext) {
    fun getDatabaseBuilder(): RoomDatabase.Builder<ComicViewerDatabase>
}

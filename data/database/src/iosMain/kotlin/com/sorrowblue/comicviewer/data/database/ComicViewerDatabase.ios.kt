package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabaseConstructor

internal actual object ComicViewerDatabaseConstructor :
    RoomDatabaseConstructor<ComicViewerDatabase> {
    actual override fun initialize(): ComicViewerDatabase {
        throw RuntimeException("Stub!")
    }
}

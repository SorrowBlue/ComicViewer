package com.sorrowblue.comicviewer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.Bookshelf
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFile
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.PasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.ReadLaterFile

@Database(
    entities = [Bookshelf::class, File::class, Favorite::class, FavoriteFile::class, ReadLaterFile::class],
    version = 1
)
@TypeConverters(PasswordConverters::class)
internal abstract class ComicViewerDatabase : RoomDatabase() {

    abstract fun bookshelfDao(): BookshelfDao

    abstract fun fileDao(): FileDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun favoriteFileDao(): FavoriteFileDao

    abstract fun readLaterFileDao(): ReadLaterFileDao
}

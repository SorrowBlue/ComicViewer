package com.sorrowblue.comicviewer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sorrowblue.comicviewer.data.database.dao.FavoriteBookDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterDao
import com.sorrowblue.comicviewer.data.database.dao.ServerDao
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.FavoriteBook
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.PasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.ReadLater
import com.sorrowblue.comicviewer.data.database.entity.Server

@Database(entities = [Server::class, File::class, Favorite::class, FavoriteBook::class, ReadLater::class], version = 1)
@TypeConverters(PasswordConverters::class)
internal abstract class ComicViewerDatabase : RoomDatabase() {

    abstract fun serverDao(): ServerDao

    abstract fun fileDao(): FileDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun favoriteBookDao(): FavoriteBookDao

    abstract fun readLaterDao(): ReadLaterDao
}

package com.sorrowblue.comicviewer.data.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionFileDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.PasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionFileEntity
import com.sorrowblue.comicviewer.data.database.entity.favorite.FavoriteEntity
import com.sorrowblue.comicviewer.data.database.entity.favorite.FavoriteFileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.readlater.ReadLaterFileEntity

internal const val DATABASE_VERSION = 7
internal const val DATABASE_NAME = "comic_viewer_database"

@Database(
    entities = [
        BookshelfEntity::class,
        FileEntity::class,
        FavoriteEntity::class,
        FavoriteFileEntity::class,
        CollectionEntity::class,
        CollectionFileEntity::class,
        ReadLaterFileEntity::class
    ],
    version = DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4),
        AutoMigration(4, 5),
        AutoMigration(5, 6),
        AutoMigration(6, 7),
    ],
    exportSchema = true
)
@ConstructedBy(ComicViewerDatabaseConstructor::class)
@TypeConverters(PasswordConverters::class)
internal abstract class ComicViewerDatabase : RoomDatabase() {

    abstract fun bookshelfDao(): BookshelfDao

    abstract fun fileDao(): FileDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun collectionDao(): CollectionDao

    abstract fun collectionFileDao(): CollectionFileDao

    abstract fun favoriteFileDao(): FavoriteFileDao

    abstract fun readLaterFileDao(): ReadLaterFileDao
}

internal expect object ComicViewerDatabaseConstructor :
    RoomDatabaseConstructor<ComicViewerDatabase> {
    override fun initialize(): ComicViewerDatabase
}

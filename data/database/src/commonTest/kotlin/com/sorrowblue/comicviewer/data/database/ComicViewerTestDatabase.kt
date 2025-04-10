package com.sorrowblue.comicviewer.data.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteDao
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.ReadLaterFileDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionFileEntity
import com.sorrowblue.comicviewer.data.database.entity.favorite.FavoriteEntity
import com.sorrowblue.comicviewer.data.database.entity.favorite.FavoriteFileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.readlater.ReadLaterFileEntity

@Database(
    entities = [BookshelfEntity::class, FileEntity::class, FavoriteEntity::class, FavoriteFileEntity::class, ReadLaterFileEntity::class, CollectionEntity::class, CollectionFileEntity::class],
    version = DATABASE_VERSION,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4),
        AutoMigration(4, 5),
        AutoMigration(5, 6),
        AutoMigration(6, 7),
    ]
)
@ConstructedBy(ComicViewerTestDatabaseConstructor::class)
@TypeConverters(FakePasswordConverters::class)
internal abstract class ComicViewerTestDatabase : RoomDatabase() {

    abstract fun bookshelfDao(): BookshelfDao

    abstract fun fileDao(): FileDao

    abstract fun favoriteDao(): FavoriteDao

    abstract fun collectionDao(): CollectionDao

    abstract fun favoriteFileDao(): FavoriteFileDao

    abstract fun readLaterFileDao(): ReadLaterFileDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
internal expect object ComicViewerTestDatabaseConstructor :
    RoomDatabaseConstructor<ComicViewerTestDatabase> {
    override fun initialize(): ComicViewerTestDatabase
}

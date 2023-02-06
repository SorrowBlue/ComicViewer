package com.sorrowblue.comicviewer.data.database.di

import android.content.Context
import androidx.room.Room
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ComicViewerDatabase =
        Room.databaseBuilder(context, ComicViewerDatabase::class.java, "comic_viewer_database")
            .build()

    @Singleton
    @Provides
    fun provideBookshelfDao(db: ComicViewerDatabase) = db.bookshelfDao()

    @Singleton
    @Provides
    fun provideFileDao(db: ComicViewerDatabase) = db.fileDao()

    @Singleton
    @Provides
    fun provideFavoriteDao(db: ComicViewerDatabase) = db.favoriteDao()

    @Singleton
    @Provides
    fun provideFavoriteFileDao(db: ComicViewerDatabase) = db.favoriteFileDao()

    @Singleton
    @Provides
    fun provideReadLaterFileDao(db: ComicViewerDatabase) = db.readLaterFileDao()
}

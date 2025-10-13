package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.TestDatabaseHelper
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.CryptUtil
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPasswordConverters
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.FakeCryptUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import jakarta.inject.Singleton

@Module
@Configuration
@ComponentScan("com.sorrowblue.comicviewer.data.database")
internal class DatabaseModule

@Module
@Configuration
internal class OverrideDatabaseModule {

    @Singleton
    internal fun providesRoomDatabaseForTest(
        helper: TestDatabaseHelper,
        decryptedPasswordConverters: DecryptedPasswordConverters,
    ): ComicViewerDatabase {
        return helper.getDatabaseBuilder()
            .addMigrations(ComicViewerDatabase.ManualMigration7to8())
            .addTypeConverter(decryptedPasswordConverters)
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .build()
    }

    @Singleton
    fun providesFakeCryptUtil(): CryptUtil = FakeCryptUtil()
}

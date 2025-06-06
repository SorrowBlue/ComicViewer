package com.sorrowblue.comicviewer.data.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.platform.app.InstrumentationRegistry

internal actual val AutoMigration_2_3_Impl: Migration =
    ComicViewerDatabase_AutoMigration_2_3_Impl()

internal actual val AutoMigration_3_4_Impl: Migration =
    ComicViewerDatabase_AutoMigration_3_4_Impl()

internal actual val AutoMigration_4_5_Impl: Migration =
    ComicViewerDatabase_AutoMigration_4_5_Impl()

internal actual val AutoMigration_5_6_Impl: Migration =
    ComicViewerDatabase_AutoMigration_5_6_Impl()

internal actual val AutoMigration_6_7_Impl: Migration =
    ComicViewerDatabase_AutoMigration_6_7_Impl()

internal actual fun getMigrationTestHelper(): MigrationTestHelper {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    return MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        file = instrumentation.context.filesDir.resolve(TEST_DB_NAME),
        driver = AndroidSQLiteDriver(),
        databaseClass = ComicViewerDatabase::class,
    )
}

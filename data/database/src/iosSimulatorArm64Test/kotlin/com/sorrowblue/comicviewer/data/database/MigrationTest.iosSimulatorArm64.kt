package com.sorrowblue.comicviewer.data.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

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
    return MigrationTestHelper(
        schemaDirectoryPath = "schemas",
        fileName = TEST_DB_NAME,
        driver = BundledSQLiteDriver(),
        databaseClass = ComicViewerDatabase::class
    )
}

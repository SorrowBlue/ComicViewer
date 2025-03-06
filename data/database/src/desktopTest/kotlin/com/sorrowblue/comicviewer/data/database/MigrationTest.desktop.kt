package com.sorrowblue.comicviewer.data.database

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import org.koin.core.annotation.Singleton
import org.koin.mp.KoinPlatform

internal actual val AutoMigration_2_3_Impl: Migration =
    ComicViewerTestDatabase_AutoMigration_2_3_Impl()

internal actual val AutoMigration_3_4_Impl: Migration =
    ComicViewerTestDatabase_AutoMigration_3_4_Impl()

internal actual val AutoMigration_4_5_Impl: Migration =
    ComicViewerTestDatabase_AutoMigration_4_5_Impl()

internal actual val AutoMigration_5_6_Impl: Migration =
    ComicViewerTestDatabase_AutoMigration_5_6_Impl()

internal actual fun getMigrationTestHelper(): MigrationTestHelper {
    val dbPath = KoinPlatform.getKoin().get<DesktopContext>().filesDir.resolve("database")
    return MigrationTestHelper(
        schemaDirectoryPath = Path("schemas"),
        databasePath = dbPath.resolve(TEST_DB_NAME),
        driver = BundledSQLiteDriver(),
        databaseClass = ComicViewerTestDatabase::class
    )
}

@Singleton
internal class TestDesktopContext : DesktopContext {

    private val os by lazy { System.getProperty("os.name").lowercase() }
    private val currentTime = System.currentTimeMillis().toString()

    override val filesDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(USER_HOME)}\\AppData\\Local",
                "Temp",
                "$IDENTIFIER-$currentTime"
            )

            os.contains("mac") -> Path(
                System.getProperty(USER_HOME),
                "Library",
                "Caches",
                "$IDENTIFIER-$currentTime"
            )

            else -> Path(System.getProperty(USER_HOME), ".cache", "$IDENTIFIER-$currentTime")
        }.also {
            if (it.notExists()) {
                it.createDirectories()
            }
        }

    override val cacheDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(USER_HOME)}\\AppData\\Local",
                "Temp",
                "$IDENTIFIER-cache-$currentTime"
            )

            os.contains("mac") -> Path(
                System.getProperty(USER_HOME),
                "Library",
                "Caches",
                "$IDENTIFIER-cache-$currentTime"
            )

            else -> Path(System.getProperty(USER_HOME), ".cache", "$IDENTIFIER-cache-$currentTime")
        }.also {
            if (it.notExists()) {
                it.createDirectories()
            }
        }

    companion object {
        private const val IDENTIFIER = "com.sorrowblue.comicviewer"
        private const val USER_HOME = "user.home"
    }
}

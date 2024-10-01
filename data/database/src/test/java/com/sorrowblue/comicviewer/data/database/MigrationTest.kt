package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_DB = "migration-test"

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        databaseClass = ComicViewerDatabase::class.java,
    )

    @Test
    fun migrateAll() {
        helper.createDatabase(TEST_DB, 1).apply {
            Truth.assertThat(version).isEqualTo(1)
            close()
        }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ComicViewerDatabase::class.java,
            TEST_DB
        ).build().apply {
            Truth.assertThat(openHelper.readableDatabase.version).isEqualTo(DATABASE_VERSION)
            close()
        }
    }

}

/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.di

import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.OnGraphClose
import dev.zacsweers.metro.SingleIn

/**
 * Graph lifecycle callbacks for closing the Room database when the graph closes.
 */
@OptIn(ExperimentalMetroApi::class)
@ContributesTo(AppScope::class)
interface DatabaseGraphLifecycle {

    /**
     * Closes the attached database instance if it has been initialized.
     *
     * @param holder Holder managing the active database reference.
     */
    @OnGraphClose
    fun closeDatabase(holder: Holder) {
        holder.close()
    }

    /**
     * Tracks an initialized database instance so it can be closed on graph teardown
     * without forcing eager database creation when unused.
     */
    @SingleIn(AppScope::class)
    @Inject
    class Holder internal constructor() : AutoCloseable {
        private var database: ComicViewerDatabase? = null

        internal fun attach(database: ComicViewerDatabase): ComicViewerDatabase {
            this.database = database
            return database
        }

        override fun close() {
            database?.close()
            database = null
        }
    }
}

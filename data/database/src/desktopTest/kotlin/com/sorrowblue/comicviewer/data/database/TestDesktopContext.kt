package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.framework.common.DesktopContext
import dev.zacsweers.metro.Inject
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

@Inject
internal class TestDesktopContext : DesktopContext() {
    private val os by lazy { System.getProperty("os.name").lowercase() }
    private val currentTime = System.currentTimeMillis().toString()

    override val filesDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(USER_HOME)}\\AppData\\Local",
                "Temp",
                "$IDENTIFIER-$currentTime",
            )

            os.contains("mac") -> Path(
                System.getProperty(USER_HOME),
                "Library",
                "Caches",
                "$IDENTIFIER-$currentTime",
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
                "$IDENTIFIER-cache-$currentTime",
            )

            os.contains("mac") -> Path(
                System.getProperty(USER_HOME),
                "Library",
                "Caches",
                "$IDENTIFIER-cache-$currentTime",
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

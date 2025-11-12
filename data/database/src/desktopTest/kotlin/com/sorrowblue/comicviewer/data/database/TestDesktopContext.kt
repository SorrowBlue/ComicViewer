package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.framework.common.DesktopContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

internal class TestDesktopContext : DesktopContext() {
    private val os by lazy { System.getProperty("os.name").lowercase() }
    private val currentTime = System.currentTimeMillis().toString()

    override val filesDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(UserHome)}\\AppData\\Local",
                "Temp",
                "$IDENTIFIER-$currentTime",
            )

            os.contains("mac") -> Path(
                System.getProperty(UserHome),
                "Library",
                "Caches",
                "$IDENTIFIER-$currentTime",
            )

            else -> Path(System.getProperty(UserHome), ".cache", "$IDENTIFIER-$currentTime")
        }.also {
            if (it.notExists()) {
                it.createDirectories()
            }
        }

    override val cacheDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(UserHome)}\\AppData\\Local",
                "Temp",
                "$IDENTIFIER-cache-$currentTime",
            )

            os.contains("mac") -> Path(
                System.getProperty(UserHome),
                "Library",
                "Caches",
                "$IDENTIFIER-cache-$currentTime",
            )

            else -> Path(System.getProperty(UserHome), ".cache", "$IDENTIFIER-cache-$currentTime")
        }.also {
            if (it.notExists()) {
                it.createDirectories()
            }
        }

    companion object {
        private const val IDENTIFIER = "com.sorrowblue.comicviewer"
        private const val UserHome = "user.home"
    }
}

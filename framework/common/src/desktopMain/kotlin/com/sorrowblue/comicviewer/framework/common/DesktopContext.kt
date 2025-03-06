package com.sorrowblue.comicviewer.framework.common

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import org.koin.core.annotation.Singleton

interface DesktopContext {
    val filesDir: Path
    val cacheDir: Path
}

@Singleton
internal class DesktopContextImpl : DesktopContext {

    private val os by lazy { System.getProperty("os.name").lowercase() }

    override val filesDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(USER_HOME)}\\AppData\\Local",
                IDENTIFIER
            )

            os.contains("mac") -> Path(
                System.getProperty(USER_HOME),
                "Library",
                "Application Support",
                IDENTIFIER
            )

            else -> Path(System.getProperty(USER_HOME), IDENTIFIER)
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
                IDENTIFIER
            )

            os.contains("mac") -> Path(
                System.getProperty(USER_HOME),
                "Library",
                "Caches",
                IDENTIFIER
            )

            else -> Path(System.getProperty(USER_HOME), ".cache", IDENTIFIER)
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

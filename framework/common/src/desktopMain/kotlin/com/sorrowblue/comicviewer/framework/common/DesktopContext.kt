package com.sorrowblue.comicviewer.framework.common

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

@Suppress("AbstractClassCanBeInterface")
abstract class DesktopContext {
    abstract val filesDir: Path
    abstract val cacheDir: Path
    lateinit var platformGraph: PlatformGraph

    companion object {
        fun init(): DesktopContext {
            return DesktopContextImpl()
        }
    }
}

private class DesktopContextImpl : DesktopContext() {
    private val os by lazy { System.getProperty("os.name").lowercase() }

    override val filesDir: Path
        get() = when {
            os.contains("win") -> Path(
                System.getenv("APPDATA") ?: "${System.getProperty(UserHome)}\\AppData\\Local",
                IDENTIFIER,
            )

            os.contains("mac") -> Path(
                System.getProperty(UserHome),
                "Library",
                "Application Support",
                IDENTIFIER,
            )

            else -> Path(System.getProperty(UserHome), IDENTIFIER)
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
                IDENTIFIER,
            )

            os.contains("mac") -> Path(
                System.getProperty(UserHome),
                "Library",
                "Caches",
                IDENTIFIER,
            )

            else -> Path(System.getProperty(UserHome), ".cache", IDENTIFIER)
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

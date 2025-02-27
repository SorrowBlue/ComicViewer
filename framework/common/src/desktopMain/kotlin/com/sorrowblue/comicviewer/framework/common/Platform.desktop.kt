package com.sorrowblue.comicviewer.framework.common

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

actual val isTouchable = false

fun getAppSpecificConfigDirectory(configDirName: String): Path {
    val os = System.getProperty("os.name").lowercase()

    val configPath = when {
        os.contains("win") -> {
            val appData =
                System.getenv("APPDATA") ?: "${System.getProperty("user.home")}\\AppData\\Local"
            Path(appData, "ComicViewerForWin", configDirName)
        }

        os.contains("mac") -> {
            Path(System.getProperty("user.home"), "Library", "Application Support", "ComicViewerForMac", configDirName)
        }

        else -> {
            Path(System.getProperty("user.home"), ".comicviewerforlinux", configDirName)
        }
    }

    if (!configPath.exists()) {
        configPath.createDirectories()
    }

    return configPath
}

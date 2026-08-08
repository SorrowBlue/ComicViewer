/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.utils.Platform
import io.github.vinceglb.filekit.utils.PlatformUtil
import io.github.vinceglb.filekit.utils.div
import io.github.vinceglb.filekit.utils.toFile
import io.github.vinceglb.filekit.utils.toPath
import java.io.File

private fun cacheDirFixed(appId: String): File {
    val folder = when (PlatformUtil.current) {
        Platform.Linux -> System.getenv("XDG_CACHE_HOME")?.let { it.toPath() / appId }
            ?: (getEnv("HOME").toPath() / ".cache" / appId)

        Platform.MacOS -> getEnv("HOME").toPath() / "Library" / "Caches" / appId

        Platform.Windows -> getEnv("LOCALAPPDATA").toPath() / "Temp" / appId
    }
    return folder.toFile()
}

private fun getEnv(key: String): String = System.getenv(key)
    ?: throw IllegalStateException("Environment variable $key not found.")

fun initFileKit(appId: String) {
    FileKit.init(appId = appId, cacheDir = cacheDirFixed(appId))
}

/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations

private val logger = Logging.getLogger("com.sorrowblue.comicviewer.AndroidVersionCodeValueSource")

interface AndroidVersionCodeParameters : ValueSourceParameters

/**
 * android versionCodeを取得するプロバイダー
 */
abstract class AndroidVersionCodeValueSource @Inject constructor(
    private val execOperations: ExecOperations,
) : ValueSource<String, AndroidVersionCodeParameters> {

    override fun obtain(): String = runCatching {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine("git", "tag")
            standardOutput = stdout
            errorOutput = stderr
        }
        val count = stdout.toString().split(Regex("\r?\n")).count { it.trim().isNotEmpty() }
        if (result.exitValue == 0) {
            (DEFAULT_VERSION_CODE + count).toString()
        } else {
            logger.warn(
                """
                Warning: Could not get git tag count.
                   Exit code: ${result.exitValue} 
                   stderr: ${stderr.toString().trim()}
                """.trimIndent(),
            )
            "$DEFAULT_VERSION_CODE"
        }
    }.onFailure {
        logger.warn("Warning: Failed to execute git command: ${it.message}")
    }.getOrDefault("$DEFAULT_VERSION_CODE")
}

private const val DEFAULT_VERSION_CODE = 2000100

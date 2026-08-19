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

private val logger = Logging.getLogger("com.sorrowblue.comicviewer.GitPlaneTagValueSource")

interface GitPlaneTagParameters : ValueSourceParameters

/**
 * git tagを取得するプロバイダー
 *
 * - v1.2.3
 * - v1.2.3-148-g3132
 * - v1.2.3-beta.1
 * - v1.2.3-beta.1-471-g3132
 */
abstract class GitPlaneTagValueSource @Inject constructor(
    private val execOperations: ExecOperations,
) : ValueSource<String, GitPlaneTagParameters> {
    override fun obtain(): String = runCatching {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine("git", "describe", "--tags", "--abbrev=1")
            isIgnoreExitValue = true
            standardOutput = stdout
            errorOutput = stderr
        }

        if (result.exitValue == 0) {
            stdout.toString().trim()
        } else {
            logger.warn(
                """
                Warning: Could not get git tag.
                   Exit code: ${result.exitValue} 
                   stderr: ${stderr.toString().trim()}
                """.trimIndent(),
            )
            "v0.0.0"
        }
    }.onFailure {
        logger.warn("Warning: Failed to execute git command: ${it.message}")
    }.getOrDefault("v0.0.0")
}

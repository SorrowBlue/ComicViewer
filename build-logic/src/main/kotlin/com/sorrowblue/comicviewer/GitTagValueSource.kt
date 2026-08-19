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

private val logger = Logging.getLogger("com.sorrowblue.comicviewer.GitTagValueSource")

interface GitTagParameters : ValueSourceParameters

/**
 * Gitタグを取得するプロバイダー（JVM用）
 *
 * - v2.0.0
 * - v2.0.0-148-g3132fb674
 */
abstract class GitTagValueSource @Inject constructor(private val execOperations: ExecOperations) :
    ValueSource<String, GitTagParameters> {
    override fun obtain(): String = runCatching {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine(
                "git",
                "describe",
                "--tags",
                "--match",
                "v[0-9]*.[0-9]*.0",
                "--exclude",
                "*beta*",
            )
            standardOutput = stdout
            isIgnoreExitValue = true
            errorOutput = stderr
        }

        if (result.exitValue != 0) {
            logger.error(
                "Warning: Could not get git tag. (Exit code: ${result.exitValue}). stderr: ${
                    stderr.toString().trim()
                }",
            )
            return "v0.0.0"
        }
        stdout.toString().trim()
    }.onFailure {
        logger.error("Warning: Failed to execute git command: ${it.message}")
    }.getOrDefault("v0.0.0")
}

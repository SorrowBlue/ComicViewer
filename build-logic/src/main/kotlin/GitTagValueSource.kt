/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

import java.io.ByteArrayOutputStream
import javax.inject.Inject
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.internal.cc.base.logger
import org.gradle.process.ExecOperations

interface GitTagParameters : ValueSourceParameters

/**
 * Gitタグを取得するプロバイダー（ベータタグを含める / JVM用）
 * 開発フライト時には、開始タグ vX.Y.0 からの直線コミット総数を自動測定して埋め込む。
 */
abstract class GitTagValueSource @Inject constructor(private val execOperations: ExecOperations) :
    ValueSource<String, GitTagParameters> {
    override fun obtain(): String = runCatching {
        val stdout = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine("git", "describe", "--tags", "--abbrev=1")
            standardOutput = stdout
            isIgnoreExitValue = true
            errorOutput = ByteArrayOutputStream()
        }

        if (result.exitValue != 0) {
            logger.error("Warning: Could not get git tag. (Exit code: ${result.exitValue})")
            return "UNKNOWN"
        }

        val rawTag = stdout.toString().trim()

        // 正規表現でタグ名と進捗を解析
        // 例: "v2.0.99-9-gabcdef" or "v2.0.0"
        val regex = Regex("""^v(\d+)\.(\d+)\.(\d+)(?:-(\d+)-g([0-9a-fA-F]+))?$""")
        val matchResult = regex.matchEntire(rawTag) ?: return rawTag

        val major = matchResult.groupValues[1]
        val minor = matchResult.groupValues[2]
        val distanceStr = matchResult.groupValues[4]
        val hash = matchResult.groupValues[5]

        if (distanceStr.isEmpty()) {
            // コミット距離なし (正式タグジャスト)
            rawTag
        } else {
            // コミット距離あり (開発フライト時)
            // 開始タグ vMajor.Minor.0 から HEAD までの直線総コミット数を取得する
            val startTag = "v$major.$minor.0"
            val revStdout = ByteArrayOutputStream()
            val revResult = execOperations.exec {
                commandLine("git", "rev-list", "--count", "$startTag..HEAD")
                standardOutput = revStdout
                isIgnoreExitValue = true
                errorOutput = ByteArrayOutputStream()
            }

            val totalDistance = if (revResult.exitValue == 0) {
                revStdout.toString().trim().toIntOrNull() ?: 0
            } else {
                distanceStr.toIntOrNull() ?: 0
            }

            "v$major.$minor.0-$totalDistance-g$hash"
        }
    }.onFailure {
        logger.error("Warning: Failed to execute git command: ${it.message}")
    }.getOrDefault("UNKNOWN")
}

/**
 * ベータタグを除外し、正式タグ（v*.*.* のみ）を取得するプロバイダー（Android用）
 */
abstract class GitFormalTagValueSource @Inject constructor(
    private val execOperations: ExecOperations,
) : ValueSource<String, GitTagParameters> {
    override fun obtain(): String = runCatching {
        val stdout = ByteArrayOutputStream()
        val result = execOperations.exec {
            // --match と --exclude を使ってベータタグを完全に除外して正式タグのみにマッチさせる
            commandLine(
                "git",
                "describe",
                "--tags",
                "--abbrev=1",
                "--match",
                "v[0-9]*.[0-9]*.[0-9]*",
                "--exclude",
                "*beta*",
            )
            standardOutput = stdout
            isIgnoreExitValue = true
            errorOutput = ByteArrayOutputStream()
        }

        if (result.exitValue == 0) {
            stdout.toString().trim()
        } else {
            logger.error("Warning: Could not get git formal tag. (Exit code: ${result.exitValue})")
            "UNKNOWN"
        }
    }.onFailure {
        logger.error("Warning: Failed to execute git formal command: ${it.message}")
    }.getOrDefault("UNKNOWN")
}

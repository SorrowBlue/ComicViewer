/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import GitTagValueSource
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.internal.cc.base.logger
import org.gradle.kotlin.dsl.of

/**
 * Git タグプロバイダーを取得する拡張プロパティ（ベータタグを含む / JVM用）。
 */
val Project.gitTagProvider: Provider<String>
    get() = providers.of(GitTagValueSource::class) {}

/**
 * 正式タグプロバイダーを取得する拡張プロパティ（ベータタグを除外 / Android用）。
 */
val Project.gitFormalTagProvider: Provider<String>
    get() = providers.of(GitFormalTagValueSource::class) {}

/**
 * Git タグからパッケージバージョンを抽出します。
 * Git タグ（例: "v1.2.3" または "1.2.3-beta.1"）をパッケージバージョン形式に変換します。
 * フォーマット: MAJOR.MINOR.BUILD
 */
fun extractPackageVersion(versionName: String): String {
    if (versionName == "UNKNOWN" || versionName.isBlank()) {
        return "1.0.0"
    }
    
    val formalTagRegex = Regex("""^v\d+\.\d+\.\d+$""")
    val formalTagWithDistanceRegex = Regex("""^v\d+\.\d+\.\d+-\d+-g[0-9a-fA-F]+$""")
    
    if (!formalTagRegex.matches(versionName) && !formalTagWithDistanceRegex.matches(versionName)) {
        throw IllegalArgumentException("Invalid git tag format: '$versionName'. Expected formal tag (vX.Y.Z) or formal tag with distance (vX.Y.Z-N-gHash).")
    }
    
    val cleanName = versionName.substring(1)
    val parts = cleanName.split("-")
    val baseVersion = parts[0]
    val baseParts = baseVersion.split(".")
    
    val major = baseParts[0].toIntOrNull() ?: 1
    val minor = baseParts[1].toIntOrNull() ?: 0
    val patch = baseParts[2].toIntOrNull() ?: 0
    
    val distance = if (parts.size > 1) parts[1].toIntOrNull() ?: 0 else 0
    
    val newPatch = if (distance == 0) {
        patch
    } else {
        1000 + distance
    }
    
    return "$major.$minor.$newPatch"
}

/**
 * バージョン名から Android の versionCode を計算します。
 * セマンティックバージョン（例: "v1.2.3" または "v1.2.3-beta.1"）を整数に変換します。
 */
fun calculateVersionCode(versionName: String): Int {
    val withoutPrefix = if (versionName.startsWith("v")) {
        versionName.substring(1)
    } else {
        versionName
    }
    val versionParts = withoutPrefix.split("-")
    val baseVersion = versionParts[0]
    val suffix = if (versionParts.size > 1) versionParts[1] else null
    val parts = baseVersion.split(".")
    return if (parts.size >= 3) {
        val major = parts[0].toIntOrNull() ?: 0
        val minor = parts[1].toIntOrNull() ?: 0
        val patch = parts[2].toIntOrNull() ?: 0
        val boundedMajor = major.coerceIn(0, 99)
        val boundedMinor = minor.coerceIn(0, 99)
        val boundedPatch = patch.coerceIn(0, 99)
        val baseVersionCode = boundedMajor * 10000 + boundedMinor * 100 + boundedPatch
        if (suffix != null && suffix.startsWith("beta.")) {
            val betaNumber = suffix.substring(5).toIntOrNull() ?: 1
            val boundedBeta = betaNumber.coerceIn(1, 98)
            logger.lifecycle("#calculateVersionCode versionName=$versionName, major=$major, minor=$minor, patch=$patch, betaNumber=$betaNumber, returning versionCode=${baseVersionCode * 100 + boundedBeta}")
            baseVersionCode * 100 + boundedBeta
        } else {
            logger.lifecycle("#calculateVersionCode versionName=$versionName, major=$major, minor=$minor, patch=$patch, returning versionCode=${baseVersionCode * 100 + 99}")
            baseVersionCode * 100 + 99
        }
    } else {
        logger.lifecycle("#calculateVersionCode versionName=$versionName, invalid format, returning versionCode=10000")
        1
    }
}

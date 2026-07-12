/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import GitTagValueSource
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.of

/**
 * Git タグプロバイダーを取得する拡張プロパティ。
 */
val Project.gitTagProvider: Provider<String>
    get() = providers.of(GitTagValueSource::class) {}

/**
 * Git タグからパッケージバージョンを抽出します。
 * Git タグ（例: "v1.2.3" または "1.2.3-beta.1"）をパッケージバージョン形式に変換します。
 * フォーマット: MAJOR.MINOR.BUILD
 */
fun extractPackageVersion(versionName: String): String {
    val withoutPrefix = if (versionName.startsWith("v")) {
        versionName.substring(1)
    } else {
        versionName
    }
    val versionParts = withoutPrefix.split("-", limit = 2)
    val baseVersion = versionParts[0]
    val parts = baseVersion.split(".")
    return if (parts.size >= 3) {
        val major = parts[0].toIntOrNull()?.coerceAtLeast(1) ?: 1
        val minor = parts[1].toIntOrNull() ?: 0
        val build = parts[2].toIntOrNull() ?: 0
        "$major.$minor.$build"
    } else {
        "1.0.0"
    }
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
            baseVersionCode * 100 + boundedBeta
        } else {
            baseVersionCode * 100 + 99
        }
    } else {
        1
    }
}

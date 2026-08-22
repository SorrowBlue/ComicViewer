/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

private val VersionTagRegex = Regex("""^v(\d+)\.(\d+)\.(\d+)(?:-(\d+)-g[0-9a-fA-F]+)?$""")

/**
 * Git タグからパッケージバージョンを抽出します。
 * Git タグ（例: "v1.2.3" または "1.2.3-beta.1"）をパッケージバージョン形式に変換します。
 * フォーマット: MAJOR.MINOR.BUILD
 */
fun extractPackageVersion(versionName: String): String {
    if (versionName == "v0.0.0" || versionName.isBlank()) {
        return "0.0.0"
    }

    val matchResult = VersionTagRegex.matchEntire(versionName)
    requireNotNull(matchResult) {
        "Invalid git tag format: '$versionName'. Expected formal tag (vX.Y.Z) or formal tag with distance (vX.Y.Z-N-gHash)."
    }

    val (majorStr, minorStr, patchStr) = matchResult.destructured
    val major = majorStr.toInt()
    val minor = minorStr.toInt()
    val patch = patchStr.toInt()

    val distance = matchResult.groups[4]?.value?.toIntOrNull() ?: 0

    val newPatch = if (distance == 0) {
        patch
    } else {
        1000 + distance
    }

    return "$major.$minor.$newPatch"
}


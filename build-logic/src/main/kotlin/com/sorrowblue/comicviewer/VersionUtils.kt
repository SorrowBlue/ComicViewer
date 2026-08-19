/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

/**
 * Git タグからパッケージバージョンを抽出します。
 * Git タグ（例: "v1.2.3" または "1.2.3-beta.1"）をパッケージバージョン形式に変換します。
 * フォーマット: MAJOR.MINOR.BUILD
 */
fun extractPackageVersion(versionName: String): String {
    if (versionName == "v0.0.0" || versionName.isBlank()) {
        return "0.0.0"
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

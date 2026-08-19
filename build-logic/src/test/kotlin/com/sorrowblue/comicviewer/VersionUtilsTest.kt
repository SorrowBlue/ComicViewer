package com.sorrowblue.comicviewer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VersionUtilsTest {

    @Test
    fun testExtractPackageVersion() {
        // 1. 正式タグ (vX.Y.Z, 距離 0) -> オフセットなしの綺麗バージョン
        assertEquals("2.0.0", extractPackageVersion("v2.0.0"))
        assertEquals("2.0.1", extractPackageVersion("v2.0.1"))
        assertEquals("2.0.2", extractPackageVersion("v2.0.2"))

        // 2. main開発中テスト (コミット距離 distance > 0)
        // 開始タグ vX.Y.0 からの直線総コミット数が distance として渡されてくる
        assertEquals("2.0.1005", extractPackageVersion("v2.0.0-5-gabcdef"))
        assertEquals("2.0.1015", extractPackageVersion("v2.0.0-15-g1234567"))

        // コミット数が非常に進んだ状態のテストフライト (例: 距離 734)
        assertEquals("2.0.1734", extractPackageVersion("v2.0.0-734-gabcdef"))

        // コミット数が 64000 まで進んだ極端なフライトリリース -> 65535の上限に綺麗に収まることを確認
        assertEquals("2.0.65000", extractPackageVersion("v2.0.0-64000-gabcdef")) // 1000 + 64000 = 65000

        // 3. 不明な場合や空文字の場合 -> 許容されるフォールバック
        assertEquals("1.0.0", extractPackageVersion("UNKNOWN"))
        assertEquals("1.0.0", extractPackageVersion(""))
    }

    @Test
    fun testExtractPackageVersionInvalidFormat() {
        // 不正フォーマット時に例外がスローされることを検証
        assertFailsWith<IllegalArgumentException> {
            extractPackageVersion("v2.0.0-beta.1")
        }
        assertFailsWith<IllegalArgumentException> {
            extractPackageVersion("v2.0.0-beta.1-3-g123456")
        }
        assertFailsWith<IllegalArgumentException> {
            extractPackageVersion("invalid-format-string")
        }
    }

    @Test
    fun testCalculateVersionCode() {
        // 公式リリース: (major * 10000 + minor * 100 + patch) * 100 + 99
        assertEquals(2000099, calculateVersionCode("v2.0.0"))
        assertEquals(2000199, calculateVersionCode("v2.0.1"))
        
        // ベータリリース: (major * 10000 + minor * 100 + patch) * 100 + beta_number
        assertEquals(2000001, calculateVersionCode("v2.0.0-beta.1"))
        assertEquals(2000022, calculateVersionCode("v2.0.0-beta.22"))
    }
}

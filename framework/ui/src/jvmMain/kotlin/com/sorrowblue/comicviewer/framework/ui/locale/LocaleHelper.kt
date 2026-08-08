/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.locale

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.resolve
import java.util.Properties
import java.util.Locale as JavaLocale

@SingleIn(AppScope::class)
@Inject
class LocaleHelper(private val context: PlatformContext) {
    private val file by lazy {
        FileKit.filesDir.resolve("lang.properties").file.apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }

    /**
     * [locale]を保存する。nullの場合は削除する。
     *
     * @param locale [JavaLocale]
     */
    fun save(locale: JavaLocale?) {
        file.outputStream().use {
            Properties().apply {
                if (locale != null) {
                    put(KeyTag, locale.toLanguageTag())
                }
                store(it, "")
            }
        }
    }

    fun load(): JavaLocale? = runCatching {
        file
            .inputStream()
            .use {
                Properties()
                    .apply {
                        load(it)
                    }.getProperty(KeyTag, null)
            }?.let {
                JavaLocale.forLanguageTag(it)
            }
    }.getOrNull()
}

private const val KeyTag = "language_tag"

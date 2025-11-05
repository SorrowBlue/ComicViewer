package com.sorrowblue.comicviewer.framework.designsystem.locale

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.util.Properties
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import java.util.Locale as JavaLocale

@SingleIn(AppScope::class)
@Inject
class LocaleHelper(private val context: PlatformContext) {

    private val file by lazy {
        context.filesDir.resolve("lang.properties").apply {
            if (!exists()) {
                createFile()
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
                    put(KEY_TAG, locale.toLanguageTag())
                }
                store(it, "")
            }
        }
    }

    fun load(): JavaLocale? {
        return kotlin.runCatching {
            file.inputStream().use {
                Properties().apply {
                    load(it)
                }.getProperty(KEY_TAG, null)
            }?.let {
                JavaLocale.forLanguageTag(it)
            }
        }.getOrNull()
    }
}

private const val KEY_TAG = "language_tag"

package com.sorrowblue.comicviewer.feature.settings

expect class LocaleManager() {
    fun currentLocale(): String

    fun setSystemDefault()

    fun setLocale(tag: String)
}

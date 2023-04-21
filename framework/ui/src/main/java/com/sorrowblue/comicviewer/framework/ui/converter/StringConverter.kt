package com.sorrowblue.comicviewer.framework.ui.converter

object StringConverter {

    @JvmStatic
    fun String.removeExtension(): String {
        val lastIndex = lastIndexOf('.')
        if (lastIndex != -1) {
            return substring(0, lastIndex)
        }
        return this
    }
}

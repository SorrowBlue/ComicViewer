package com.sorrowblue.comicviewer.bookshelf.manage

import android.content.Context

class RequireValidator : TextValidator() {

    override fun validate(value: String): Boolean {
        return value.isNotBlank()
    }

    override fun error(context: Context, value: String): String {
        return "この項目は入力必須です。"
    }
}

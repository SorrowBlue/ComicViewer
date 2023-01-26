package com.sorrowblue.comicviewer.server.management.util

import android.content.Context

class DomainNameTextValidator : TextValidator() {

    override fun validate(value: String): Boolean {
        return value.isEmpty() || value.matches("^([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}\$".toRegex())
    }

    override fun error(context: Context, value: String): String {
        return "正しいドメイン名を入力してください"
    }
}

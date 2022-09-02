package com.sorrowblue.comicviewer.management.util

import android.content.Context

class HostNameTextValidator : TextValidator() {
    override fun validate(value: String): Boolean {
        return value.matches("^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])\$".toRegex())
    }

    override fun error(context: Context, value: String): String {
        return "ホスト名またはIPアドレスを入力してください"
    }
}

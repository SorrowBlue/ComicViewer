package com.sorrowblue.comicviewer.server.management.util

import android.content.Context

class PortTextValidator : TextValidator() {
    override fun validate(value: String): Boolean {
        return value.matches("^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))\$".toRegex())
    }

    override fun error(context: Context, value: String): String {
        return "0～65535の間で入力してください"
    }
}

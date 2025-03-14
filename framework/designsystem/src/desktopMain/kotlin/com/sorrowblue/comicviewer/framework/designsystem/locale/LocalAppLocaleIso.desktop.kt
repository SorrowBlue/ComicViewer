package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale


actual object LocalAppLocaleIso {
    private var default: Locale? = null
    private val LocalAppLocaleIso = staticCompositionLocalOf { Locale.getDefault().toString() }
    actual val current: String
        @Composable get() = LocalAppLocaleIso.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        if (default == null) {
            default = Locale.getDefault()
        }
        val new = when(value) {
            null -> default!!
            else -> Locale(value)
        }
        Locale.setDefault(new)
        return LocalAppLocaleIso.provides(new.toString())
    }
}
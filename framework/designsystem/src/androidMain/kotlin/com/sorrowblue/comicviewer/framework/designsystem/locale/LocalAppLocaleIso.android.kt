package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import java.util.Locale
import logcat.logcat

actual object LocalAppLocaleIso {

    actual val locales: List<String> get() = LocaleListCompat.getDefault().run {
        List(size()) {
            get(it)?.displayName
        }.filterNotNull()
    }

    private var default: Locale? = null
    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        AppCompatDelegate.getApplicationLocales().also {
            logcat { "ApplicationLocales: " + it.toLanguageTags() }
            logcat { "getEmptyLocaleList: " + LocaleListCompat.getEmptyLocaleList().toLanguageTags() }
            logcat { "getDefault: " + LocaleListCompat.getDefault().toLanguageTags() }
            logcat { "getAdjustedDefault: " + LocaleListCompat.getAdjustedDefault().toLanguageTags() }
        }
        val configuration = LocalConfiguration.current

        if (default == null) {
            default = Locale.getDefault()
        }

        val new = when (value) {
            null -> default!!
            else -> Locale(value)
        }
        logcat { "Set locale after: '$new'. before: $current.  default: $default" }
        Locale.setDefault(new)
        configuration.setLocale(new)
        LocalContext.current.createConfigurationContext(configuration)
        return LocalConfiguration.provides(configuration)
    }
}

actual fun updateAppLocaleIso(value: String?) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(value))
}
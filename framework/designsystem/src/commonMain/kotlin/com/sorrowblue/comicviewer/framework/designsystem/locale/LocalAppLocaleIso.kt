package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

var customAppLocaleIso by mutableStateOf<String?>(null)

expect fun updateAppLocaleIso(value: String?)

expect object LocalAppLocaleIso {
    val current: String @Composable get
    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
    val locales: List<String>
}

val ProvideLocalAppLocaleIso @Composable get() =
    LocalAppLocaleIso provides customAppLocaleIso
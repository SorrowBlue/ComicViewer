package com.sorrowblue.comicviewer.feature.settings.inapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.toImmutableList

@Composable
context(context: InAppLanguagePickerScreenContext)
internal fun InAppLanguagePickerScreenRoot(onBackClick: () -> Unit) {
    val locales = remember { context.appLocaleIso.locales.toImmutableList() }
    InAppLanguagePickerScreen(
        locales,
        onBackClick = onBackClick,
    )
}

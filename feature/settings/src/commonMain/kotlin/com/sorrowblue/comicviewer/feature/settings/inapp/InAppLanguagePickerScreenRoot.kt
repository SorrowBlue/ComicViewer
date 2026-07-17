/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.inapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun InAppLanguagePickerScreenRoot(
    onBackClick: () -> Unit,
    viewModel: InAppLanguagePickerViewModel = metroViewModel<InAppLanguagePickerViewModel>(),
) {
    val locales = remember { viewModel.appLocaleIso.locales.toImmutableList() }
    InAppLanguagePickerScreen(
        appLocaleIso = viewModel.appLocaleIso,
        localeList = locales,
        onBackClick = onBackClick,
    )
}

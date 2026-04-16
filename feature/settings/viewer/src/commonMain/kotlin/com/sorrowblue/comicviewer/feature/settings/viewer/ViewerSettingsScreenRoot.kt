package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.dropUnlessResumed
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection.BindingDirectionScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.NavigationResultEffect

@Composable
context(context: ViewerSettingsScreenContext)
internal fun ViewerSettingsScreenRoot(
    onBackClick: () -> Unit,
    onBindingDirectionClick: (BindingDirection) -> Unit,
) {
    val state = rememberViewerSettingsScreenState()
    ViewerSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onStatusBarShowChange = dropUnlessResumed(block = state::onStatusBarShowChange),
        onNavigationBarShowChange = dropUnlessResumed(block = state::onNavigationBarShowChange),
        onTurnOnScreenChange = dropUnlessResumed(block = state::onTurnOnScreenChange),
        onCutWhitespaceChange = dropUnlessResumed(block = state::onCutWhitespaceChange),
        onDisplayFirstPageChange = dropUnlessResumed(block = state::onDisplayFirstPageChange),
        onBindingDirectionClick = { onBindingDirectionClick(state.uiState.bindingDirection) },
        onPreloadPagesChange = dropUnlessResumed(block = state::onPreloadPagesChange),
        onImageQualityChange = dropUnlessResumed(block = state::onImageQualityChange),
        onFixScreenBrightnessChange = dropUnlessResumed(block = state::onFixScreenBrightnessChange),
        onScreenBrightnessChange = dropUnlessResumed(block = state::onScreenBrightnessChange),
        modifier = Modifier.testTag("ViewerSettingsRoot"),
    )

    NavigationResultEffect(BindingDirectionScreenResultKey, state::onBindingDirectionScreenResult)
}

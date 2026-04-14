package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.BindingDirectionScreenResultKey
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
        onStatusBarShowChange = state::onStatusBarShowChange,
        onNavigationBarShowChange = state::onNavigationBarShowChange,
        onTurnOnScreenChange = state::onTurnOnScreenChange,
        onCutWhitespaceChange = state::onCutWhitespaceChange,
        onDisplayFirstPageChange = state::onDisplayFirstPageChange,
        onBindingDirectionClick = { onBindingDirectionClick(state.uiState.bindingDirection) },
        onPreloadPagesChange = state::onPreloadPagesChange,
        onImageQualityChange = state::onImageQualityChange,
        onFixScreenBrightnessChange = state::onFixScreenBrightnessChange,
        onScreenBrightnessChange = state::onScreenBrightnessChange,
        modifier = Modifier.testTag("ViewerSettingsRoot"),
    )

    NavigationResultEffect(BindingDirectionScreenResultKey, state::onBindingDirectionScreenResult)
}

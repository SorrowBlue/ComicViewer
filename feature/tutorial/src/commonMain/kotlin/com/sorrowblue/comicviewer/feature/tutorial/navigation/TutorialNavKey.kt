/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.tutorial.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenRoot
import com.sorrowblue.comicviewer.feature.tutorial.nav.TutorialNavKey
import com.sorrowblue.comicviewer.framework.navigation.AppContentDecoratorProvider
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun tutorialNavEntry(navigator: Navigator) {
    scope.entry<TutorialNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
        TutorialScreenRoot(onComplete = navigator::goBack)
    }
}

@ContributesIntoSet(AppScope::class)
internal class TutorialAppContentDecoratorProvider : AppContentDecoratorProvider {

    override val order = 0

    @Composable
    override fun Content(
        isInitialized: Boolean,
        onInitialize: () -> Unit,
        content: @Composable () -> Unit,
    ) {
        val viewModel = metroViewModel<TutorialAppContentViewModel>()
        val currentOnInitialize by rememberUpdatedState(onInitialize)
        val tutorialRequired by viewModel.tutorialRequired.collectAsStateWithLifecycle(false)
        if (tutorialRequired) {
            TutorialScreenRoot(onComplete = viewModel::completeTutorial)
            SideEffect(viewModel) {
                currentOnInitialize()
            }
        } else {
            content()
        }
    }
}

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class TutorialAppContentViewModel(val loadSettingsUseCase: LoadSettingsUseCase) :
    ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val tutorialRequired = loadSettingsUseCase.settings
        .mapLatest { !it.doneTutorial }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun completeTutorial() {
        viewModelScope.launch {
            loadSettingsUseCase.edit { it.copy(doneTutorial = true) }
        }
    }
}

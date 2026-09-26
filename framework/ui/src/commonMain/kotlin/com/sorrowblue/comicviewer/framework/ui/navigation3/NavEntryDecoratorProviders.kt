/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.navigation3

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.result.rememberResultEventBusNavEntryDecorator
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet

@ContributesIntoSet(AppScope::class)
internal class SaveableStateHolderNavEntryDecorator : NavEntryDecoratorProvider {
    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> =
        rememberSaveableStateHolderNavEntryDecorator()
}

@ContributesIntoSet(AppScope::class)
internal class ResultEventBusNavEntryDecorator : NavEntryDecoratorProvider {
    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> =
        rememberResultEventBusNavEntryDecorator()
}

@ContributesIntoSet(AppScope::class)
internal class ViewModelStoreNavEntryDecorator : NavEntryDecoratorProvider {
    @Composable
    override fun rememberNavEntryDecorator(): NavEntryDecorator<NavKey> =
        rememberViewModelStoreNavEntryDecorator()
}

/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.permission.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.permission.LocalNetworkAccessPermissionScreenRoot
import com.sorrowblue.comicviewer.feature.permission.nav.LocalNetworkAccessPermissionNavKey
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntry
import com.sorrowblue.comicviewer.framework.navigation.Navigator

@NavigationEntry
context(scope: EntryProviderScope<NavKey>)
internal fun localNetworkAccessPermissionNavEntry(navigator: Navigator) {
    scope.entry<LocalNetworkAccessPermissionNavKey>(
        metadata = DialogSceneStrategy.dialog(),
    ) {
        LocalNetworkAccessPermissionScreenRoot(
            onDismissRequest = navigator::goBack,
        )
    }
}

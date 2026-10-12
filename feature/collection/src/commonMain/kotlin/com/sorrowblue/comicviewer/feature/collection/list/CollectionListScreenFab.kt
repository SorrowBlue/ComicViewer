/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.list

import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.feature.collection.nav.BasicCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.nav.SmartCollectionCreateNavKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionListNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.FloatingActionContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.FloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.adaptive.isVisible
import com.sorrowblue.comicviewer.framework.ui.adaptive.removeFloatingActionButtonMenuPadding
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_collection
import comicviewer.feature.collection.generated.resources.collection_label_smart_collection
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import org.jetbrains.compose.resources.stringResource

@ContributesIntoSet(AppScope::class)
internal class CollectionListScreenFab : FloatingActionContent {
    override val navKey = CollectionListNavKey::class

    override val allowRail = false

    @Composable
    override fun Content(
        state: FloatingActionButtonState,
        modifier: Modifier,
        elevation: FloatingActionButtonElevation,
        expanded: Boolean,
    ) {
        val navigator = LocalNavigator.current
        FloatingActionButtonMenu(
            modifier = modifier.removeFloatingActionButtonMenuPadding(),
            expanded = state.menuExpanded && state.targetValue.isVisible,
            button = {
                ToggleFloatingActionButton(
                    modifier =
                        Modifier
                            .testTag("FloatingActionButton")
                            .animateFloatingActionButton(
                                visible =
                                    state.targetValue.isVisible &&
                                        (state.targetValue.isVisible || state.menuExpanded),
                                alignment = Alignment.Center,
                            ),
                    checked = state.menuExpanded,
                    containerSize = ToggleFloatingActionButtonDefaults.containerSize(),
                    onCheckedChange = state::toggleMenu,
                ) {
                    val imageVector by remember(checkedProgress) {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) ComicIcons.Close else ComicIcons.Edit
                        }
                    }
                    Icon(
                        imageVector,
                        contentDescription = null,
                        modifier = Modifier.animateIcon(checkedProgress = { checkedProgress }),
                    )
                }
            },
        ) {
            FloatingActionButtonMenuItem(
                onClick = {
                    state.toggleMenu(false)
                    navigator.navigate(BasicCollectionCreateNavKey())
                },
                text = {
                    Text(stringResource(Res.string.collection_label_collection))
                },
                icon = {
                    Icon(
                        imageVector = ComicIcons.Favorite,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.testTag("BasicCollectionCreateButton"),
            )
            FloatingActionButtonMenuItem(
                onClick = {
                    state.toggleMenu(false)
                    navigator.navigate(SmartCollectionCreateNavKey())
                },
                text = {
                    Text(stringResource(Res.string.collection_label_smart_collection))
                },
                icon = {
                    Icon(
                        imageVector = ComicIcons.CollectionsBookmark,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.testTag("SmartCollectionCreateButton"),
            )
        }
    }
}

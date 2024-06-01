package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.ElevationTokens
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        ComicTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceColorAtElevation(ElevationTokens.Level2),
        FastOutLinearInEasing.transform(colorTransitionFraction)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownMenuChip(
    text: String,
    onChangeSelect: (T) -> Unit,
    menus: PersistentList<T>,
    menu: (T) -> String,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val colorTransitionFraction = scrollBehavior.state.overlappedFraction
    val fraction = if (colorTransitionFraction > 0.01f) 1f else 0f
    val appBarContainerColor by animateColorAsState(
        targetValue = containerColor(fraction),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "DropdownMenuChipColorAnimation"
    )
    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        var expanded by remember { mutableStateOf(false) }
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            label = { Text(text = text) },
            trailingIcon = {
                Icon(
                    imageVector = ComicIcons.ArrowDropDown,
                    contentDescription = null
                )
            },
            colors = FilterChipDefaults.filterChipColors(containerColor = appBarContainerColor)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menus.forEach {
                DropdownMenuItem(
                    text = { Text(text = menu(it)) },
                    onClick = {
                        onChangeSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

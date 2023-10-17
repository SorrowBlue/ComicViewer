package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SelectableChipBorder
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.FilterChip as MaterialFilterChip

@Composable
@ExperimentalMaterial3Api
fun SingleChoiceChipRowScope.FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = FilterChipDefaults.shape,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.filterChipElevation(),
    border: SelectableChipBorder? = FilterChipDefaults.filterChipBorder(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    MaterialFilterChip(
        selected,
        onClick,
        label,
        modifier,
        enabled,
        leadingIcon,
        trailingIcon,
        shape,
        colors,
        elevation,
        border,
        interactionSource
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SingleChoiceChipRow(
    modifier: Modifier = Modifier,
    space: Dp = 8.dp,
    content: @Composable SingleChoiceChipRowScope.() -> Unit,
) {
    FlowRow(
        modifier = modifier
            .selectableGroup()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space)
    ) {
        val scope = remember { SingleChoiceChipRowScopeWrapper(this) }
        scope.content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
private class SingleChoiceChipRowScopeWrapper(scope: FlowRowScope) :
    SingleChoiceChipRowScope, FlowRowScope by scope

interface SingleChoiceChipRowScope : RowScope

package com.sorrowblue.comicviewer.app.component

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.app.MainScreenFab
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
class AppFabState(private val scope: CoroutineScope) {
    var mainScreenFab by mutableStateOf<MainScreenFab?>(null)

    var isShown by mutableStateOf(false)

    fun show(mainScreenFab: MainScreenFab) {
        if (isShown) {
            isShown = false
            scope.launch {
                delay(MotionTokens.DurationMedium1 + 50L)
                this@AppFabState.mainScreenFab = mainScreenFab
                isShown = true
            }
        } else {
            this.mainScreenFab = mainScreenFab
            isShown = true
        }
    }

    fun hide() {
        isShown = false
    }
}

@Composable
internal fun rememberAppFabState(
    scope: CoroutineScope = rememberCoroutineScope(),
) = remember {
    AppFabState(scope)
}

@Composable
internal fun AppFab(
    state: AppFabState,
    canScroll: Boolean,
    onClick: () -> Unit,
) {
    ExtendedFloatingActionButton(
        expanded = !canScroll,
        text = {
            if (state.mainScreenFab != null) {
                Text(text = stringResource(id = state.mainScreenFab!!.label))
            }
        },
        icon = {
            if (state.mainScreenFab != null) {
                Icon(
                    imageVector = state.mainScreenFab!!.icon,
                    contentDescription = stringResource(id = state.mainScreenFab!!.contentDescription)
                )
            }
        },
        onClick = onClick,
    )
}

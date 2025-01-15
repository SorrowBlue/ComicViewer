package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * A class which provides easy-to-use utilities for updating the System UI
 * bar colors within Jetpack Compose.
 *
 * @sample com.google.accompanist.sample.systemuicontroller.SystemUiControllerSample
 */
@Stable
interface SystemUiController {

    /**
     * Control for the behavior of the system bars. This value should
     * be one of the [WindowInsetsControllerCompat] behavior constants:
     * [WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_TOUCH]
     * (Deprecated), [WindowInsetsControllerCompat.BEHAVIOR_DEFAULT] and
     * [WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE].
     */
    var systemBarsBehavior: Int

    /**
     * Property which holds the status bar visibility. If set to true, show the
     * status bar, otherwise hide the status bar.
     */
    var isStatusBarVisible: Boolean

    /**
     * Property which holds the navigation bar visibility. If set to true, show
     * the navigation bar, otherwise hide the navigation bar.
     */
    var isNavigationBarVisible: Boolean

    /**
     * Property which holds the status & navigation bar visibility. If set to
     * true, show both bars, otherwise hide both bars.
     */
    var isSystemBarsVisible: Boolean
        get() = isNavigationBarVisible && isStatusBarVisible
        set(value) {
            isStatusBarVisible = value
            isNavigationBarVisible = value
        }
}

@Composable
expect fun rememberSystemUiController(): SystemUiController

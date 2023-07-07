package com.sorrowblue.comicviewer.framework.ui.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.DialogFragmentNavigator
import com.sorrowblue.comicviewer.framework.ui.navigation.FrameworkFragmentNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel(),
    NavController.OnDestinationChangedListener {

    val snackbarMessage = MutableSharedFlow<String>(0, 1, BufferOverflow.SUSPEND)
    var shouldKeepOnScreen = true

    val isRestored = MutableSharedFlow<Boolean>(1)

    val isVisibleBottomNav = MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 1)
    val fabState = MutableSharedFlow<FabState>(replay = 1, extraBufferCapacity = 1)

    override fun onDestinationChanged(
        controller: NavController, destination: NavDestination, arguments: Bundle?
    ) {
        if (destination is FrameworkFragmentNavigator.Destination) {
            isVisibleBottomNav.tryEmit(destination.isVisibleBottomNavigation)
            if (destination.isVisibleFab) {
                fabState.tryEmit(FabState.Show(destination.fabIcon, destination.fabLabel))
            } else {
                fabState.tryEmit(FabState.Hide)
            }
        } else if (destination is DialogFragmentNavigator.Destination) {
            fabState.tryEmit(FabState.Hide)
        } else {
            isVisibleBottomNav.tryEmit(false)
            fabState.tryEmit(FabState.Hide)
        }

    }
}

sealed interface FabState {
    val isShown: Boolean

    class Show(val iconResId: Int, val labelResId: Int, val isEnabled: Boolean = true) : FabState {
        override val isShown = true
    }

    data object Hide : FabState {
        override val isShown = true
    }
}

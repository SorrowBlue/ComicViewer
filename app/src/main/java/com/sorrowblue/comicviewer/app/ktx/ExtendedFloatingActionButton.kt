package com.sorrowblue.comicviewer.app.ktx

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.sorrowblue.comicviewer.framework.ui.fragment.FabState

fun ExtendedFloatingActionButton.setState(state: FabState?) {
    if (state is FabState.Show) {
        if (isShown) {
            hide(object : ExtendedFloatingActionButton.OnChangedCallback() {
                override fun onHidden(extendedFab: ExtendedFloatingActionButton?) {
                    setText(state.labelResId)
                    setIconResource(state.iconResId)
                    isEnabled = state.isEnabled
                    show()
                }
            })
        } else {
            setText(state.labelResId)
            setIconResource(state.iconResId)
            isEnabled = state.isEnabled
            show()
        }
    } else {
        setOnClickListener(null)
        hide()
    }
}

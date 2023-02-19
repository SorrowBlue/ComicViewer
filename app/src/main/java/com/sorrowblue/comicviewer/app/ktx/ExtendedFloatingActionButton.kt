package com.sorrowblue.comicviewer.app.ktx

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import logcat.logcat

fun ExtendedFloatingActionButton.isShownWithImageResource(isShown: Boolean, iconResId: Int, labelResid: Int) {
    logcat { "isShownWithImageResource($isShown, $iconResId, $labelResid)" }
    if (isShown) {
        if (this.isShown) {
            hide(object : ExtendedFloatingActionButton.OnChangedCallback() {
                override fun onHidden(extendedFab: ExtendedFloatingActionButton?) {
                    setText(labelResid)
                    setIconResource(iconResId)
                    show()
                }
            })
        } else {
            setText(labelResid)
            setIconResource(iconResId)
            show()
        }
    } else {
        setOnClickListener(null)
        hide()
    }
}

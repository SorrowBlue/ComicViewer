package com.sorrowblue.comicviewer.app.ktx

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

fun ExtendedFloatingActionButton.isShownWithImageResource(isShown: Boolean, iconResId: Int, labelResid: Int) {
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

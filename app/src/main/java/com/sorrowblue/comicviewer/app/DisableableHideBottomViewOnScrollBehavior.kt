package com.sorrowblue.comicviewer.app

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior

class DisableableHideBottomViewOnScrollBehavior<V : View> : HideBottomViewOnScrollBehavior<V> {
    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    var isEnabled = true

    override fun slideUp(child: V, animate: Boolean) {
        if (isEnabled) {
            super.slideUp(child, animate)
        }
    }
}


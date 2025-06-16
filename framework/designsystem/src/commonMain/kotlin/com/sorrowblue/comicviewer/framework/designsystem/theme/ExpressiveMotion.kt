package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.animation.core.spring

object ExpressiveMotion {
    object Spatial {
        fun <T> fast() = spring<T>(0.6f, 800f)
        fun <T> default() = spring<T>(0.8f, 380f)
        fun <T> slow() = spring<T>(0.8f, 200f)
    }

    object Effects {
        fun <T> fast() = spring<T>(1f, 3800f)
        fun <T> default() = spring<T>(1f, 1600f)
        fun <T> slow() = spring<T>(1f, 800f)
    }
}

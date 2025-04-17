package com.sorrowblue.comicviewer.framework.ui.navigation

import kotlin.reflect.KClass

interface TabDisplayRoute {
    val routes: List<KClass<*>>
}

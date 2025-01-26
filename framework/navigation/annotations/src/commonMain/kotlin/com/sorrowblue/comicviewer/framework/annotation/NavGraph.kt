package com.sorrowblue.comicviewer.framework.annotation

import com.sorrowblue.comicviewer.framework.navigation.NavTransition
import kotlin.reflect.KClass

annotation class NavGraph(
    val startDestination: KClass<*>,
    val root: KClass<*> = Nothing::class,
    val transition: KClass<out NavTransition> = NavTransition.Default::class,
)

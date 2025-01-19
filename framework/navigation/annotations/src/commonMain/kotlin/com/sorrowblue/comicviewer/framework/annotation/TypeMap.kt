package com.sorrowblue.comicviewer.framework.annotation

import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import kotlin.reflect.KClass
import org.koin.core.annotation.Named
import org.koin.core.annotation.Scope

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Destination<T : Any>(
    val style: KClass<out DestinationStyle> = DestinationStyle.Composable::class,
)

@Named
annotation class DestinationImpl

annotation class NavGraph<T : Any>(
    val startDestination: KClass<*>,
    val root: KClass<*> = Nothing::class,
)

annotation class DestinationInGraph<T : Any>
annotation class NestedNavGraph<T : Any>

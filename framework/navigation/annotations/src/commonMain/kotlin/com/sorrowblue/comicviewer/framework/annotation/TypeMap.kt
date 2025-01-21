package com.sorrowblue.comicviewer.framework.annotation

import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Destination<T : @Serializable Any>(
    val style: KClass<out DestinationStyle> = DestinationStyle.Composable::class,
)


annotation class NavGraph(
    val startDestination: KClass<*>,
    val root: KClass<*> = Nothing::class,
)

annotation class DestinationInGraph<T : Any>
annotation class NestedNavGraph<T : Any>

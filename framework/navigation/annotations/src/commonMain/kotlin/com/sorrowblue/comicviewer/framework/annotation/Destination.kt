package com.sorrowblue.comicviewer.framework.annotation

import com.sorrowblue.comicviewer.framework.navigation.DestinationStyle
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Destination<T : @Serializable Any>(
    val style: KClass<out DestinationStyle> = DestinationStyle.Composable::class,
)

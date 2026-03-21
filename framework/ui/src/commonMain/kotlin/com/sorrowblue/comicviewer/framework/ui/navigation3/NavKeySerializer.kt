package com.sorrowblue.comicviewer.framework.ui.navigation3

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

typealias NavKeyEntry = Pair<KClass<NavKey>, KSerializer<NavKey>>
typealias ScreenEntryProvider = EntryProviderScope<NavKey>.(Navigator) -> Unit

val NavKeyEntry.subclass: KClass<NavKey>
    get() = first

val NavKeyEntry.serializer: KSerializer<NavKey>
    get() = second

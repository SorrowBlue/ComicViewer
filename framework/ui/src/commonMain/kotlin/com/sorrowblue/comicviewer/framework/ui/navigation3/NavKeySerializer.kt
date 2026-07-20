/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.navigation3

import androidx.navigation3.runtime.NavKey
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

typealias NavKeyEntry = Pair<KClass<NavKey>, KSerializer<NavKey>>

val NavKeyEntry.subclass: KClass<NavKey>
    get() = first

val NavKeyEntry.serializer: KSerializer<NavKey>
    get() = second

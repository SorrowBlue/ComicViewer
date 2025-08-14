package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = Dummy::class,
    destinations = [Dummy::class]
)
@Serializable
actual object ReceiveBookNavGraph

@Serializable
internal data object Dummy

@Destination<Dummy>
@Composable
internal fun DummyScreen() = Unit

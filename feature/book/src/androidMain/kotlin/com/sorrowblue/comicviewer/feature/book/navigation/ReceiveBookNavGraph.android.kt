package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBook
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = ReceiveBook::class,
    destinations = [ReceiveBook::class]
)
@Serializable
actual data object ReceiveBookNavGraph

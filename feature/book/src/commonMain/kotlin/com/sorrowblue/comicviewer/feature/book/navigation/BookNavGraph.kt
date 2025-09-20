package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.book.menu.BookMenu
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = Book::class,
    destinations = [Book::class, BookMenu::class],
    transitions = NavTransitions.ApplyParent::class
)
@Serializable
data object BookNavGraph

expect object ReceiveBookNavGraph

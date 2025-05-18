package com.sorrowblue.comicviewer.feature.book.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.book.menu.BookMenu
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = Book::class,
    transitions = BookNavGraphTransitions::class,
    destinations = [Book::class, BookMenu::class]
)
@Serializable
data object BookNavGraph

package com.sorrowblue.comicviewer.aggregation.navigation

import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraphTransitions
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphTransitions
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraphTransitions
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraphTransitions
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphTransitions
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraphTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object AggregationNavTransitions : DestinationTransitions() {
    override val transitions: List<TransitionsConfigure> = buildList {
        addAll(SettingsNavGraphTransitions.transitions)
        addAll(BookNavGraphTransitions.transitions)
        addAll(BookshelfNavGraphTransitions.transitions)
        addAll(CollectionNavGraphTransitions.transitions)
        addAll(ReadLaterNavGraphTransitions.transitions)
        addAll(HistoryNavGraphTransitions.transitions)
    }
}

package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.history.navgraphs.HistoryNavGraph
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryGraphDependencies
import com.sorrowblue.comicviewer.feature.library.LibraryScreenNavigator
import com.sorrowblue.comicviewer.feature.library.NavGraphs
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph

@Composable
fun DependenciesContainerBuilder<*>.LibraryGraphDependencies(
    onSettingsClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    navigateToBook: (Book, FavoriteId?) -> Unit,
    onFavoriteClick: (BookshelfId, String) -> Unit,
) {
    navGraph(NavGraphs.library) {
        dependency(
            object : LibraryScreenNavigator {
                override fun onFeatureClick(feature: Feature) {
                    when (feature) {
                        is Feature.AddOn -> {
                            when (feature) {
                                is Feature.AddOn.Box -> BoxNavGraph()
                                is Feature.AddOn.Dropbox -> DropBoxNavGraph()
                                is Feature.AddOn.GoogleDrive -> GoogleDriveNavGraph()
                                is Feature.AddOn.OneDrive -> OneDriveNavGraph()
                            }?.let {
                                destinationsNavigator.navigate(it.direction)
                            }
                        }

                        Feature.Basic.History -> destinationsNavigator.navigate(HistoryNavGraph)
                    }
                }
            }
        )
        HistoryGraphDependencies(
            onSettingsClick = onSettingsClick,
            navigateToBook = { navigateToBook(it, null) },
            onFavoriteClick = onFavoriteClick,
            onSearchClick = onSearchClick,
            onBookClick = navigateToBook,
        )
    }
}

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraph
import com.sorrowblue.comicviewer.bookshelf.navigation.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.bookshelf.navigation.bookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.domain.model.AddOn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraphNavigator
import com.sorrowblue.comicviewer.favorite.navigation.favoriteNavGraphNavigator
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavGraph
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavGraphNavigator
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.authentication.navigation.authenticationNavGraphNavigator
import com.sorrowblue.comicviewer.feature.book.destinations.BookScreenDestination
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraphNavigator
import com.sorrowblue.comicviewer.feature.book.navigation.bookNavGraphNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraph
import com.sorrowblue.comicviewer.feature.library.navigation.LibraryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.library.navigation.libraryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.readLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.navigation.searchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.settings.destinations.SettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraphNavigator
import com.sorrowblue.comicviewer.feature.settings.navigation.dependencySettingsNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.destinations.TutorialScreenDestination
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraphNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.tutorialNavGraphNavigator
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator

fun DependenciesContainerBuilder<*>.mainDependency(
    addOnList: SnapshotStateList<AddOn>,
    onRestoreComplete: () -> Unit,
    onBack: () -> Unit,
    onAuthCompleted: (Boolean) -> Unit,
    onTutorialExit: () -> Unit,
) {
    dependency(object : CoreNavigator {
        override fun navigateUp() {
            navController.navigateUp()
        }
    })
    dependency(BookshelfNavGraph) {
        bookshelfNavGraphNavigator(object : BookshelfNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onRestoreComplete() {
                onRestoreComplete()
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(BookNavGraph) {
        bookNavGraphNavigator(object : BookNavGraphNavigator {
            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(ReadLaterNavGraph) {
        readLaterNavGraphNavigator(object : ReadLaterNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(SearchNavGraph) {
        searchNavGraphNavigator(object : SearchNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependency(FavoriteNavGraph) {
        favoriteNavGraphNavigator(object : FavoriteNavGraphNavigator {
            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun navigateToBook(book: Book, favoriteId: FavoriteId) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name, favoriteId)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(SearchScreenDestination(bookshelfId, path))
            }

            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }
        })
    }

    dependencySettingsNavGraph(object : SettingsNavGraphNavigator {
        override fun onStartTutorialClick() {
            navController.navigate(TutorialScreenDestination)
        }

        override fun onPasswordChange() {
            navController.navigate(
                AuthenticationScreenDestination(
                    mode = Mode.Change,
                    handleBack = false
                )
            )
        }

        override fun navigateToChangeAuth(enabled: Boolean) {
            if (enabled) {
                navController.navigate(AuthenticationScreenDestination(Mode.Register, false))
            } else {
                navController.navigate(AuthenticationScreenDestination(Mode.Erase, false))
            }
        }
    })

    dependency(AuthenticationNavGraph) {
        authenticationNavGraphNavigator(object : AuthenticationNavGraphNavigator {
            override fun onBack() {
                onBack()
            }

            override fun onAuthCompleted(handleBack: Boolean, mode: Mode) {
                when (mode) {
                    Mode.Register, Mode.Change, Mode.Erase -> navController.popBackStack()
                    Mode.Authentication -> onAuthCompleted(handleBack)
                }
            }
        })
    }

    dependency(TutorialNavGraph) {
        tutorialNavGraphNavigator(object : TutorialNavGraphNavigator {
            override fun onComplete() {
                onTutorialExit()
            }
        })
    }

    dependency(LibraryNavGraph) {
        libraryNavGraphNavigator(object : LibraryNavGraphNavigator {
            override fun onSettingsClick() {
                navController.navigate(SettingsScreenDestination)
            }

            override fun navigateToBook(book: Book) {
                navController.navigate(
                    BookScreenDestination(book.bookshelfId, book.path, book.name)
                )
            }

            override fun onFavoriteClick(file: File) {
                navController.navigate(FavoriteAddScreenDestination(file.bookshelfId, file.path))
            }
        })
    }
    addOnList.forEach { it.findNavGraph()?.dependency() }
}

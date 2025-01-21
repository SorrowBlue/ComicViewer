package com.sorrowblue.comicviewer.feature.bookshelf.navigation

/*

@Composable
fun DependenciesContainerBuilder<*>.BookshelfGraphDependencies(
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (BookshelfId, String) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    navGraph(NavGraphs.bookshelf) {
        dependency(object :
            BookshelfScreenNavigator,
            BookshelfSelectionNavigator,
            BookshelfEditScreenNavigator,
            FolderScreenNavigator {

            private val navigator get() = destinationsNavigator

            override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) =
                onFavoriteClick(bookshelfId, path)

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                onSearchClick(bookshelfId, path)

            override fun onSettingsClick() = onSettingsClick()

            override fun onRestoreComplete() = onRestoreComplete()

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onBack(editMode: BookshelfEditMode) {
                when (editMode) {
                    is BookshelfEditMode.Edit ->
                        navigator.navigateUp()

                    is BookshelfEditMode.Register -> {
//                        TODO
//                    navigator.navigate(BookshelfSelectionScreenDestination) {
//                            popUpTo(BookshelfScreenDestination)
                    }
                }
            }

            override fun onFabClick() {
//                navigator.navigate(BookshelfSelectionScreenDestination)
            }

            override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
//                navigator.navigate(
//                    BookshelfFolderScreenDestination(bookshelfId, path, null)
//                )
            }

            override fun notificationRequest(type: ScanType) {
                TODO("Not yet implemented")
            }

            override fun onEditClick(id: BookshelfId) {
                TODO("Not yet implemented")
            }

            override fun onRemoveClick(bookshelfId: BookshelfId) {
                TODO("Not yet implemented")
            }

//            override fun notificationRequest(type: ScanType) {
//                navigator.navigate(NotificationRequestScreenDestination(type))
//            }
//
//            override fun onEditClick(id: BookshelfId) {
//                navigator.navigate(BookshelfEditScreenDestination(BookshelfEditMode.Edit(id)))
//            }
//
//            override fun onRemoveClick(bookshelfId: BookshelfId) {
//                navigator.navigate(
//                    BookshelfDeleteScreenDestination(bookshelfId)
//                )
//            }

            override fun onSourceClick(bookshelfType: BookshelfType) {
//                navigator.navigate(
//                    BookshelfEditScreenDestination(BookshelfEditMode.Register(bookshelfType))
//                ) {
//                    popUpTo(BookshelfScreenDestination)
//                }
            }

            override fun onComplete() {
//                if (!navigator.popBackStack(
//                        BookshelfSelectionScreenDestination,
//                        true
//                    )
//                ) {
//                    navigator.popBackStack()
//                }
            }

            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file)
                    is Folder -> {
//                        navigator.navigate(
//                            BookshelfFolderScreenDestination(file.bookshelfId, file.path, null)
//                        )
                    }
                }
            }
        })
    }
}
*/

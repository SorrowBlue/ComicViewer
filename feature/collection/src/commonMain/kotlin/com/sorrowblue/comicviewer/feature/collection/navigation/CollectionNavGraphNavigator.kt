package com.sorrowblue.comicviewer.feature.collection.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.CollectionFolder
import com.sorrowblue.comicviewer.feature.collection.CollectionFolderScreenNavigator
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenNavigator
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollection
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddNavigator
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavGraph
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.navigateToBasicCollectionCreate
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.navigateToBasicCollectionEdit
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.navigateToSmartCollectionCreate
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.navigateToSmartCollectionEdit
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenNavigator
import org.koin.core.annotation.Factory
import com.sorrowblue.comicviewer.domain.model.collection.Collection as CollectionModel

interface CollectionNavGraphNavigator {
    fun onSettingsClick()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onBookClick(book: Book, collectionId: CollectionId = CollectionId())

    fun NavController.navigateCollectionAdd(bookshelfId: BookshelfId, path: String) {
        navigate(BasicCollectionAddNavGraph(bookshelfId, path))
    }

    fun NavController.navigateSmartCollectionCreate(
        bookshelfId: BookshelfId,
        searchCondition: SearchCondition,
    ) {
        navigateToSmartCollectionCreate(
            bookshelfId = bookshelfId,
            searchCondition = searchCondition
        )
    }
}

@Factory
internal class CollectionListNavGraphNavigatorImpl(
    override val navController: NavController,
    val navigator: CollectionNavGraphNavigator,
) : CollectionScreenNavigator,
    CollectionListScreenNavigator,
    CollectionFolderScreenNavigator,
    BasicCollectionAddNavigator {

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        navigator.onSearchClick(bookshelfId, path)
    }

    override fun onSettingsClick() {
        navigator.onSettingsClick()
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigator.onBookClick(file)
            is Folder -> navController.navigate(CollectionFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun onCollectionEditClick(collection: CollectionModel) {
        when (collection) {
            is BasicCollection ->
                navController.navigateToBasicCollectionEdit(collection.id)

            is SmartCollection ->
                navController.navigateToSmartCollectionEdit(collection.id)
        }
    }

    override fun onCollectionDeleteClick(collection: CollectionModel) {
        navController.navigate(DeleteCollection(collection.id, collection.name))
    }

    override fun onFileClick(file: File, collectionId: CollectionId) {
        when (file) {
            is Book -> navigator.onBookClick(file, collectionId)
            is Folder -> navController.navigate(CollectionFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun onCollectionAddClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(BasicCollectionAddNavGraph(bookshelfId, path))
    }

    override fun onOpenFolderClick(file: File) {
        navController.navigate(CollectionFolder(file.bookshelfId, file.parent, file.path))
    }

    override fun onCreateSmartCollectionClick() {
        navController.navigateToSmartCollectionCreate()
    }

    override fun onCreateBasicCollectionClick() {
        navController.navigateToBasicCollectionCreate()
    }

    override fun navigateToCollection(id: CollectionId) {
        navController.navigate(Collection(id))
    }

    override fun onCollectionCreateClick(bookshelfId: BookshelfId, path: String) {
        navController.navigateToBasicCollectionCreate(bookshelfId, path)
    }
}

package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreate
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEdit
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreate
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEdit
import kotlinx.serialization.Serializable

@NavGraph(
    startDestination = BasicCollectionCreate::class,
    destinations = [BasicCollectionCreate::class, BasicCollectionEdit::class, SmartCollectionCreate::class, SmartCollectionEdit::class],
    transitions = NavTransitions.ApplyParent::class
)
@Serializable
data object CollectionEditorNavGraph

fun NavController.navigateToBasicCollectionCreate(
    bookshelfId: BookshelfId = BookshelfId(),
    path: String = "",
) {
    navigate(BasicCollectionCreate(bookshelfId, path))
}

fun NavController.navigateToBasicCollectionEdit(id: CollectionId) {
    navigate(BasicCollectionEdit(id))
}

fun NavController.navigateToSmartCollectionCreate(
    bookshelfId: BookshelfId = BookshelfId(),
    searchCondition: SearchCondition = SearchCondition(),
) {
    navigate(SmartCollectionCreate(bookshelfId, searchCondition))
}

fun NavController.navigateToSmartCollectionEdit(id: CollectionId) {
    navigate(SmartCollectionEdit(id))
}

package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal interface BasicCollectionAddScreenState {

    val pagingDataFlow: Flow<PagingData<Pair<Collection, Boolean>>>
    val recentFavoritesFlow: Flow<PagingData<Pair<Collection, Boolean>>>

    fun onCollectionClick(collection: Collection, exist: Boolean)
}

@Composable
internal fun rememberBasicCollectionAddScreenState(
    route: BasicCollectionAdd,
    scope: CoroutineScope = rememberCoroutineScope(),
    removeCollectionFileUseCase: RemoveCollectionFileUseCase = koinInject(),
    addCollectionFileUseCase: AddCollectionFileUseCase = koinInject(),
    viewModel: BasicCollectionAddViewModel = koinViewModel { parametersOf(route) },
): BasicCollectionAddScreenState = remember {
    BasicCollectionAddScreenStateImpl(
        route = route,
        scope = scope,
        pagingDataFlow = viewModel.pagingDataFlow,
        recentFavoritesFlow = viewModel.recentPagingDataFlow,
        removeCollectionFileUseCase = removeCollectionFileUseCase,
        addCollectionFileUseCase = addCollectionFileUseCase,
    )
}

private class BasicCollectionAddScreenStateImpl(
    override val pagingDataFlow: Flow<PagingData<Pair<Collection, Boolean>>>,
    override val recentFavoritesFlow: Flow<PagingData<Pair<Collection, Boolean>>>,
    private val route: BasicCollectionAdd,
    private val scope: CoroutineScope,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
) : BasicCollectionAddScreenState {

    override fun onCollectionClick(collection: Collection, exist: Boolean) {
        scope.launch {
            if (exist) {
                removeCollectionFileUseCase(
                    RemoveCollectionFileUseCase.Request(
                        CollectionFile(
                            collection.id,
                            route.bookshelfId,
                            route.path
                        )
                    )
                ).first()
            } else {
                addCollectionFileUseCase(
                    AddCollectionFileUseCase.Request(
                        CollectionFile(
                            collection.id,
                            route.bookshelfId,
                            route.path
                        )
                    )
                ).first()
            }
        }
    }
}

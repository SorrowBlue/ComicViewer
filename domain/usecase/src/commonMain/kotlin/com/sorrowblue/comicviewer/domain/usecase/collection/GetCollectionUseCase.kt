/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Observes a [Collection] by its [CollectionId].
 */
abstract class GetCollectionUseCase :
    UseCase<CollectionId, Collection, GetCollectionUseCase.Error>() {

    /**
     * Errors that can occur when observing a collection.
     */
    sealed interface Error : Resource.AppError {
        /**
         * Indicates that a system or database error occurred.
         */
        data object System : Error

        /**
         * Indicates that the requested collection was not found.
         */
        data object NotFound : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetCollectionUseCaseImpl(private val repository: CollectionRepository) :
    GetCollectionUseCase() {

    override fun run(request: CollectionId): Flow<Resource<Collection, Error>> =
        repository.flow(request)
            .distinctUntilChanged()
            .map<Collection?, Resource<Collection, Error>> {
                if (it == null) {
                    Resource.Error(Error.NotFound)
                } else {
                    Resource.Success(it)
                }
            }.catch { e ->
                if (e is CancellationException) throw e
                logcat(LogPriority.ERROR) { e.asLog() }
                emit(Resource.Error(Error.System))
            }
}

package com.sorrowblue.comicviewer.framework.ui.paging

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import kotlin.jvm.JvmSuppressWildcards

/**
 * Returns a factory of stable and unique keys representing the item.
 *
 * Keys are generated with the key lambda that is passed in. If null is passed in, keys will
 * default to a placeholder key. If [PagingConfig.enablePlaceholders] is true,
 * LazyPagingItems may return null items. Null items will also automatically default to
 * a placeholder key.
 *
 * This factory can be applied to Lazy foundations such as [LazyGridScope.items] or Pagers.
 * Examples:
 * @sample androidx.paging.compose.samples.PagingWithHorizontalPager
 * @sample androidx.paging.compose.samples.PagingWithLazyGrid
 *
 * @param [key] a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. When you specify the key the scroll position will be maintained
 * based on the key, which means if you add/remove items before the current visible item the
 * item with the given key will be kept as the first visible one.
 */
fun <T : Any> LazyPagingItems<T>.itemKey(
    key: ((item: @JvmSuppressWildcards T) -> Any)? = null,
): (index: Int) -> Any {
    return { index ->
        if (key == null) {
            index
        } else {
            val item = peek(index)
            if (item == null) index else key(item)
        }
    }
}

val CombinedLoadStates.isLoading
    get() = refresh is LoadState.Loading

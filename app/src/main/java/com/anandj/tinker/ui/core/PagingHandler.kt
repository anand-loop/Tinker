package com.anandj.tinker.ui.core

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PagingHandler(
    lazyListState: LazyListState,
    pagingThreshold: Int = 5,
    onLoadNextPage: () -> Unit,
) {
    val shouldLoadNextPage =
        remember {
            derivedStateOf {
                val layoutInfo = lazyListState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

                lastVisibleItemIndex > (totalItems - pagingThreshold)
            }
        }

    LaunchedEffect(shouldLoadNextPage) {
        snapshotFlow { shouldLoadNextPage.value }
            .distinctUntilChanged()
            .collect {
                Log.d("anandjdev", "LaunchedEffect: shouldLoadNextPage=$it")
                onLoadNextPage()
            }
    }
}

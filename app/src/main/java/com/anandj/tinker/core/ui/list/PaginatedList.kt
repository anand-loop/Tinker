@file:OptIn(ExperimentalMaterial3Api::class)

package com.anandj.tinker.core.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.anandj.tinker.theme.TinkerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <ItemT, ExtraT> PaginatedList(
    modifier: Modifier = Modifier,
    items: List<ItemT>,
    state: PaginatedListState<ExtraT>,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    itemContent: @Composable (ItemT) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullToRefreshState()
    val isRefreshing = state.loadingState == LoadingState.Refreshing

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        LazyColumn(
            state = lazyListState,
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(TinkerTheme.dimen.contentPadding),
            verticalArrangement = Arrangement.spacedBy(TinkerTheme.dimen.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(items) {
                itemContent(it)
            }

            item {
                if (state.loadingState == LoadingState.Paging) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxHeight(),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        if (pullRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullRefreshState.startRefresh()
            } else {
                pullRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
    PagingHandler(lazyListState = lazyListState) {
        onLoadNextPage()
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package com.anandj.tinker.core.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.anandj.tinker.R
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

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.padding(TinkerTheme.dimen.contentPadding),
            verticalArrangement = Arrangement.spacedBy(TinkerTheme.dimen.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                RefreshBar(state.loadingState, pullRefreshState) {
                    onRefresh()
                }
            }

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
    }
    PagingHandler(lazyListState = lazyListState) {
        onLoadNextPage()
    }
}

@Composable
fun RefreshBar(
    fetchState: LoadingState,
    pullRefreshState: PullToRefreshState,
    onRefresh: () -> Unit,
) {
    LaunchedEffect(key1 = pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            pullRefreshState.startRefresh()
            onRefresh()
        }
    }

    LaunchedEffect(key1 = fetchState) {
        if (pullRefreshState.isRefreshing && fetchState != LoadingState.Refreshing) {
            pullRefreshState.endRefresh()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (fetchState == LoadingState.Idle) {
            IconButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp * (1f + pullRefreshState.progress)),
                onClick = { },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_people),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .size(32.dp),
            )
        }
    }
}

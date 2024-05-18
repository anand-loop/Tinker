@file:OptIn(ExperimentalMaterial3Api::class)

package com.anandj.tinker.module.rickmorty.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anandj.tinker.R
import com.anandj.tinker.core.ui.FetchState
import com.anandj.tinker.core.ui.PagingHandler
import com.anandj.tinker.module.rickmorty.data.remote.Character
import com.anandj.tinker.theme.TinkerTheme

@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    onRouteToDetails: (id: Int) -> Unit,
) {
    val vm: CharactersViewModel = hiltViewModel()
    val state = vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effect.collect {
            handleEffect(context, it)
        }
    }

    val pullRefreshState = rememberPullToRefreshState()
    Surface(modifier = modifier) {
        CharacterList(
            characters = vm.characters,
            state = state.value,
            pullRefreshState = pullRefreshState,
            modifier = Modifier,
            onRefresh = {
                vm.sendAction(CharactersAction.Load)
            },
            onLoadNextPage = {
                vm.sendAction(CharactersAction.LoadNextPage)
            },
            onCharacterClick = { id ->
                onRouteToDetails(id)
            },
        )
    }
}

private fun handleEffect(
    context: Context,
    effect: CharactersEffect,
) {
    when (effect) {
        is CharactersEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
    }
}

@Composable
fun RefreshBar(
    fetchState: FetchState,
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
        if (pullRefreshState.isRefreshing && fetchState != FetchState.REFRESHING) {
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
        if (fetchState == FetchState.IDLE) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterList(
    modifier: Modifier = Modifier,
    characters: List<Character>,
    state: CharactersState,
    pullRefreshState: PullToRefreshState,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onCharacterClick: (characterId: Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier.padding(TinkerTheme.dimen.contentPadding),
            verticalArrangement = Arrangement.spacedBy(TinkerTheme.dimen.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                RefreshBar(state.fetchState, pullRefreshState) {
                    onRefresh()
                }
            }

            itemsIndexed(characters) { index, item ->
                CharacterCard(
                    item,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCharacterClick(item.id)
                            },
                )
            }

            item {
                if (state.fetchState == FetchState.PAGING) {
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
fun CharacterCard(
    character: Character,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(character.image)
                        .crossfade(400)
                        .build(),
                placeholder = painterResource(R.drawable.placeholder_image),
                contentDescription = null,
            )
            Column(
                modifier =
                    Modifier
                        .padding(TinkerTheme.dimen.contentPadding)
                        .weight(1.0f),
            ) {
                Text(
                    text = character.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = character.traitsLabel(),
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = character.location.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun Character.traitsLabel(): String {
    return stringResource(id = R.string.character_traits, species, gender)
}

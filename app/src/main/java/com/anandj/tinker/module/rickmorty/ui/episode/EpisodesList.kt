@file:OptIn(ExperimentalMaterial3Api::class)

package com.anandj.tinker.module.rickmorty.ui.episode

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anandj.tinker.core.ui.list.PaginatedList
import com.anandj.tinker.core.ui.list.PaginatedListState
import com.anandj.tinker.module.rickmorty.data.remote.Episode

@Composable
fun EpisodesScreen(
    modifier: Modifier = Modifier,
    onRouteToDetails: (id: Int) -> Unit,
) {
    val vm: EpisodesViewModel = hiltViewModel()
    val state = vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effect.collect {
            handleEffect(context, it)
        }
    }

    Surface(modifier = modifier) {
        EpisodeList(
            episodes = vm.list,
            state = state.value,
            modifier = Modifier,
            onRefresh = {
                vm.refresh()
            },
            onLoadNextPage = {
                vm.loadNextPage()
            },
            onEpisodeClick = { id ->
                onRouteToDetails(id)
            },
        )
    }
}

private fun handleEffect(
    context: Context,
    effect: EpisodesEffect,
) {
    when (effect) {
        is EpisodesEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun EpisodeList(
    modifier: Modifier = Modifier,
    episodes: List<Episode>,
    state: PaginatedListState<Unit>,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onEpisodeClick: (episodeId: Int) -> Unit,
) {
    PaginatedList(
        modifier = modifier,
        items = episodes,
        state = state,
        onRefresh = onRefresh,
        onLoadNextPage = onLoadNextPage,
    ) {
        EpisodeCard(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onEpisodeClick(it.id) },
            episode = it,
        )
    }
}

@Composable
fun EpisodeCard(
    episode: Episode,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = episode.name + " ${episode.created}")
    }
}

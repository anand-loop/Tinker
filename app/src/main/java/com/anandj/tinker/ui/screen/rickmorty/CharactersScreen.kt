package com.anandj.tinker.ui.screen.rickmorty

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anandj.tinker.R
import com.anandj.tinker.data.network.rickmorty.Character
import com.anandj.tinker.ui.core.FetchState
import com.anandj.tinker.ui.core.PagingHandler
import com.anandj.tinker.ui.theme.TinkerTheme

@Composable
fun CharactersScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val vm: CharactersViewModel = hiltViewModel()
    val state = vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effect.collect {
            handleEffect(context, it)
        }
    }

    CharacterList(
        characters = vm.characters,
        state = state.value,
        modifier = modifier,
        onLoadNextPage = {
            vm.sendAction(CharactersAction.LoadNextPage)
        },
        onCharacterClick = { id ->
            navController.navigate("character/$id")
        },
    )
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
private fun CharacterList(
    characters: List<Character>,
    state: CharactersState,
    modifier: Modifier = Modifier,
    onLoadNextPage: () -> Unit,
    onCharacterClick: (characterId: Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.padding(TinkerTheme.dimen.contentPadding),
        verticalArrangement = Arrangement.spacedBy(TinkerTheme.dimen.contentPadding),
    ) {
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
                Text(text = "Loading...", style = MaterialTheme.typography.titleMedium)
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
        ) {
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
                    text = character.species,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Normal,
                )
            }

            AsyncImage(
                model =
                    ImageRequest.Builder(LocalContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.placeholder_image),
                contentDescription = null,
            )
        }
    }
}

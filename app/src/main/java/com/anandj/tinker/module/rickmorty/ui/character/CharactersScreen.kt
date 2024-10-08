@file:OptIn(ExperimentalMaterial3Api::class)

package com.anandj.tinker.module.rickmorty.ui.character

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anandj.tinker.R
import com.anandj.tinker.core.ui.list.PaginatedList
import com.anandj.tinker.core.ui.list.PaginatedListState
import com.anandj.tinker.module.rickmorty.data.api.Character
import com.anandj.tinker.module.rickmorty.ui.character.render.CharacterRender
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

    Surface(modifier = modifier) {
        CharacterList(
            characters = vm.list,
            state = state.value,
            modifier = Modifier,
            onRefresh = {
                vm.refresh()
            },
            onLoadNextPage = {
                vm.loadNextPage()
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
private fun CharacterList(
    modifier: Modifier = Modifier,
    characters: List<CharacterRender>,
    state: PaginatedListState<Unit>,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onCharacterClick: (characterId: Int) -> Unit,
) {
    PaginatedList(
        modifier = modifier,
        items = characters,
        state = state,
        onRefresh = onRefresh,
        onLoadNextPage = onLoadNextPage,
    ) {
        CharacterCard(
            modifier =
                Modifier
                    .padding(bottom = TinkerTheme.dimen.contentPadding)
                    .clickable { onCharacterClick(it.id) },
            character = it,
        )

        HorizontalDivider()
    }
}

@Composable
fun CharacterCard(
    character: CharacterRender,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
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
                    .padding(horizontal = TinkerTheme.dimen.contentPadding)
                    .weight(1.0f),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = character.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
            )

            val (bg, fg) = character.status.colorPair()
            Badge(
                modifier = Modifier.padding(vertical = 4.dp),
                containerColor = bg,
                contentColor = fg,
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                    text = character.status,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = character.species,
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun String.colorPair(): Pair<Color, Color> {
    return when (this) {
        "Alive" -> Pair(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        "Dead" -> Pair(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        else -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun Character.traitsLabel(): String {
    return stringResource(id = R.string.character_traits, species, gender)
}

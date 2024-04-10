package com.anandj.tinker.ui.screen.rickmorty

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anandj.tinker.R
import com.anandj.tinker.data.network.rickmorty.Character
import com.anandj.tinker.util.GrayscaleTransformation

@Composable
fun CharacterDetailsScreen(
    modifier: Modifier = Modifier,
    id: String,
) {
    val vm: CharacterDetailsViewModel = hiltViewModel()
    val state = vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.sendAction(CharacterDetailsAction.Load(id))
        vm.effect.collect {
            handleEffect(context, it)
        }
    }

    state.value.character?.let {
        CharacterDetails(modifier = modifier, it)
    }
}

@Composable
private fun CharacterDetails(
    modifier: Modifier,
    character: Character,
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = modifier) {
        AsyncImage(
            modifier =
                Modifier.fillMaxWidth()
                    .fillMaxHeight(.40f),
            model =
                ImageRequest.Builder(LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .transformations(
                        if (character.isDead()) {
                            listOf(GrayscaleTransformation())
                        } else {
                            emptyList()
                        },
                    )
                    .build(),
            placeholder = painterResource(R.drawable.placeholder_image),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
        )

        LazyColumn(state = lazyListState) {
            item {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = character.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = character.species,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = character.status,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = character.gender,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = character.location.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
            item {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = character.origin.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun Character.isDead(): Boolean {
    return this.status == "Dead"
}

private fun handleEffect(
    context: Context,
    effect: CharacterDetailsEffect,
) {
    when (effect) {
        is CharacterDetailsEffect.ShowMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
    }
}

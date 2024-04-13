package com.anandj.tinker.module.rickmorty.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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
import com.anandj.tinker.core.draw.GrayscaleTransformation
import com.anandj.tinker.module.rickmorty.data.remote.Character

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    modifier: Modifier = Modifier,
    id: Int,
    onBack: () -> Unit = {},
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = state.value.character?.name ?: "")
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = modifier.padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            if (state.value.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                state.value.character?.let {
                    CharacterDetails(character = it)
                }
            }
        }
    }
}

@Composable
private fun CharacterDetails(
    modifier: Modifier = Modifier,
    character: Character,
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            modifier =
                Modifier
                    .fillMaxWidth()
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

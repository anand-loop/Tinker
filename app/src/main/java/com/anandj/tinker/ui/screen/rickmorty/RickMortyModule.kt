package com.anandj.tinker.ui.screen.rickmorty

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anandj.tinker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RickMortyModule() {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors =
                    topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                title = {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            stringResource(id = R.string.rick_morty_app_title),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = "characters") {
            composable(route = "characters") {
                CharactersScreen(
                    modifier = Modifier.padding(paddingValues = paddingValues),
                    onRouteToDetails = { navController.navigate("character/$it") },
                )
            }
            composable(
                "character/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: "0"

                CharacterDetailsScreen(
                    modifier = Modifier.padding(paddingValues = paddingValues),
                    id = id,
                )
            }
        }
    }
}

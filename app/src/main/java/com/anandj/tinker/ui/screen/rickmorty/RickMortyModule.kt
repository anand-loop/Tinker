package com.anandj.tinker.ui.screen.rickmorty

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun RickMortyModule() {
    val navController = rememberNavController()

    Surface {
        NavHost(
            navController = navController,
            startDestination = "characters",
        ) {
            composable(
                route = "characters",
                exitTransition = { fadeOut(tween(DURATION)) },
                popEnterTransition = { fadeIn(tween(DURATION)) },
            ) {
                CharactersScreen(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    onRouteToDetails = { navController.navigate("character/$it") },
                )
                // TestBox(Color.Red, { navController.navigate("character/1") })
            }
            composable(
                route = "character/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        tween(DURATION),
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        tween(DURATION),
                    )
                },
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: "0"

                CharacterDetailsScreen(
                    modifier = Modifier.fillMaxSize(),
                    id = id,
                )

                // TestBox(Color.Blue, {})
            }
        }
    }
}

@Composable
private fun TestBox(
    color: Color,
    onClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(color).clickable { onClick() }) {
    }
}

private const val DURATION = 300

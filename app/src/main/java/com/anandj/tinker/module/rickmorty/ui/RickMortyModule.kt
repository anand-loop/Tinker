package com.anandj.tinker.module.rickmorty.ui

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
            exitTransition = { fadeOut(tween(DURATION)) },
            popEnterTransition = { fadeIn(tween(DURATION)) },
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
        ) {
            composable(
                route = "characters",
            ) {
                CharactersScreen(
                    modifier =
                        Modifier.fillMaxSize(),
                    onRouteToDetails = { navController.navigate("character/$it") },
                )
            }
            composable(
                route = "character/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0

                CharacterDetailsScreen(
                    modifier = Modifier.fillMaxSize(),
                    id = id,
                    onBack = { navController.popBackStack() },
                )
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

package com.anandj.tinker.ui.screen.rickmorty

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun RickMortyModule() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "characters") {
        composable("characters") {
            CharactersScreen(navController = navController)
        }
        composable(
            "character/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "0"
            CharacterDetailsScreen(id = id)
        }
    }
}

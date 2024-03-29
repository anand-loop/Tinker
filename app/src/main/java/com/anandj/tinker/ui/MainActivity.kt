package com.anandj.tinker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anandj.tinker.ui.screen.rickmorty.CharactersScreen
import com.anandj.tinker.ui.theme.TinkerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinkerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    CharactersScreen()
}

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    // NavigationBar {}
}

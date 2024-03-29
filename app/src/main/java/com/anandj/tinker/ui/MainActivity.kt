package com.anandj.tinker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.anandj.tinker.ui.screen.rickmorty.RickMortyModule
import com.anandj.tinker.ui.theme.TinkerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinkerTheme {
                RickMortyModule()
            }
        }
    }
}

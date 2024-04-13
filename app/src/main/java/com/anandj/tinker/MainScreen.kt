package com.anandj.tinker

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anandj.tinker.module.rickmorty.ui.RickMortyModule
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val vm: MainViewModel = hiltViewModel()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val state = vm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                AppModule.entries.forEach { module ->
                    NavigationDrawerItem(
                        label = { Text(text = module.name) },
                        selected = state.value.module == module,
                        onClick = {
                            vm.sendAction(MainAction.LoadModule(module))
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                    )
                }
            }
        },
    ) {
        when (state.value.module) {
            AppModule.RickMorty -> RickMortyModule()
            else -> {}
        }
    }
}

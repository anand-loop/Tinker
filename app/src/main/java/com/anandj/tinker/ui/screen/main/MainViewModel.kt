package com.anandj.tinker.ui.screen.main

import com.anandj.tinker.ui.core.BaseViewModel
import com.anandj.tinker.ui.core.UiAction
import com.anandj.tinker.ui.core.UiEffect
import com.anandj.tinker.ui.core.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
    @Inject
    constructor() : BaseViewModel<MainState, MainAction, MainEffect>(MainState()) {
        override suspend fun onAction(action: MainAction) {
            when (action) {
                is MainAction.LoadModule -> onLoadModule(action.module)
            }
        }

        private fun onLoadModule(module: AppModule) {
            updateState { MainState(module) }
        }
    }

data class MainState(
    val module: AppModule = AppModule.RickMorty,
) : UiState

sealed class MainAction : UiAction {
    data class LoadModule(val module: AppModule) : MainAction()
}

sealed class MainEffect : UiEffect {
    data class ShowMessage(val message: String) : MainEffect()
}

enum class AppModule {
    Empty,
    RickMorty,
}

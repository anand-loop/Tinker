package com.anandj.tinker.module.rickmorty.ui

import androidx.lifecycle.viewModelScope
import com.anandj.tinker.core.ui.BaseViewModel
import com.anandj.tinker.core.ui.UiAction
import com.anandj.tinker.core.ui.UiEffect
import com.anandj.tinker.core.ui.UiState
import com.anandj.tinker.module.rickmorty.data.remote.Character
import com.anandj.tinker.module.rickmorty.data.remote.RickMortyApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel
    @Inject
    constructor(
        private val rickMortyApi: RickMortyApi,
    ) : BaseViewModel<CharacterDetailsState, CharacterDetailsAction, CharacterDetailsEffect>(CharacterDetailsState()) {
        override suspend fun onAction(action: CharacterDetailsAction) {
            when (action) {
                is CharacterDetailsAction.Load -> onLoad(action.id)
            }
        }

        private fun onLoad(id: String) {
            viewModelScope.launch {
                updateState { copy(isLoading = true) }

                runCatching { rickMortyApi.getCharacter(id) }
                    .onSuccess {
                        updateState { copy(character = it, isLoading = false) }
                    }
                    .onFailure {
                        CharactersEffect.ShowMessage("Error: ${it.message}")
                    }
            }
        }
    }

data class CharacterDetailsState(
    val character: Character? = null,
    val isLoading: Boolean = false,
) : UiState

sealed class CharacterDetailsAction : UiAction {
    data class Load(val id: String) : CharacterDetailsAction()
}

sealed class CharacterDetailsEffect : UiEffect {
    data class ShowMessage(val message: String) : CharacterDetailsEffect()
}

package com.anandj.tinker.ui.screen.rickmorty

import androidx.lifecycle.viewModelScope
import com.anandj.tinker.data.network.rickmorty.Character
import com.anandj.tinker.data.network.rickmorty.RickMortyApi
import com.anandj.tinker.ui.core.BaseViewModel
import com.anandj.tinker.ui.core.UiAction
import com.anandj.tinker.ui.core.UiEffect
import com.anandj.tinker.ui.core.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
            viewModelScope.launch(Dispatchers.IO) {
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

package com.anandj.tinker.module.rickmorty.ui

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.anandj.tinker.core.ui.BaseViewModel
import com.anandj.tinker.core.ui.FetchState
import com.anandj.tinker.core.ui.UiAction
import com.anandj.tinker.core.ui.UiEffect
import com.anandj.tinker.core.ui.UiState
import com.anandj.tinker.module.rickmorty.data.RickMortyRepository
import com.anandj.tinker.module.rickmorty.data.remote.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel
    @Inject
    constructor(
        private val repository: RickMortyRepository,
    ) : BaseViewModel<CharactersState, CharactersAction, CharactersEffect>(CharactersState()) {
        private val _characters = mutableStateListOf<Character>()
        val characters: List<Character> = _characters

        init {
            onLoad(nextPage = false)
        }

        override suspend fun onAction(action: CharactersAction) {
            when (action) {
                CharactersAction.Load -> onLoad(nextPage = false)
                CharactersAction.LoadNextPage -> onLoad(nextPage = true)
                is CharactersAction.EditCharacter -> onEditCharacter(action.index)
            }
        }

        private fun onEditCharacter(index: Int) {
            _characters[index] = _characters[index].copy(name = "<deleted>")
        }

        private fun onLoad(nextPage: Boolean) {
            if (state.value.fetchState != FetchState.IDLE) return

            viewModelScope.launch {
                if (nextPage) {
                    updateState { copy(fetchState = FetchState.PAGING) }
                } else {
                    updateState { copy(fetchState = FetchState.REFRESHING, nextPage = null) }
                }

                runCatching { repository.getCharacters(page = state.value.nextPage) }
                    .onSuccess {
                        if (!nextPage) {
                            _characters.clear()
                        }
                        _characters.addAll(it.results)

                        updateState {
                            copy(
                                totalCount = it.info.count,
                                nextPage = it.info.next.getPageNum(),
                                fetchState = FetchState.IDLE,
                            )
                        }
                    }
                    .onFailure {
                        CharactersEffect.ShowMessage("Error: ${it.message}")

                        if (nextPage) {
                            updateState { copy(fetchState = FetchState.PAGING_ERROR) }
                        } else {
                            updateState { copy(fetchState = FetchState.REFRESHING_ERROR) }
                        }
                    }
            }
        }
    }

private fun String?.getPageNum(): Int? {
    kotlin.runCatching {
        Uri.parse(this)
    }
        .onSuccess {
            return it.getQueryParameter("page")?.toIntOrNull()
        }

    return null
}

data class CharactersState(
    val totalCount: Int = 0,
    val nextPage: Int? = null,
    val fetchState: FetchState = FetchState.IDLE,
) : UiState

sealed class CharactersAction : UiAction {
    data object Load : CharactersAction()

    data object LoadNextPage : CharactersAction()

    data class EditCharacter(val index: Int) : CharactersAction()
}

sealed class CharactersEffect : UiEffect {
    data class ShowMessage(val message: String) : CharactersEffect()
}
package com.anandj.tinker.module.rickmorty.ui.character

import android.net.Uri
import com.anandj.tinker.core.ui.UiAction
import com.anandj.tinker.core.ui.UiEffect
import com.anandj.tinker.core.ui.list.PaginatedListState
import com.anandj.tinker.core.ui.list.PaginatedListViewModel
import com.anandj.tinker.module.rickmorty.data.RickMortyRepository
import com.anandj.tinker.module.rickmorty.data.api.Character
import com.anandj.tinker.module.rickmorty.data.api.PagedList
import com.anandj.tinker.module.rickmorty.ui.character.render.CharacterRender
import com.anandj.tinker.module.rickmorty.ui.character.render.toRender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel
    @Inject
    constructor(
        private val repository: RickMortyRepository,
    ) : PaginatedListViewModel<PagedList<Character>, CharacterRender, Unit, CharactersAction, CharactersEffect>(
            initialState = PaginatedListState(params = Unit),
        ) {
        override fun fetch(): Flow<PagedList<Character>> {
            return flow {
                val result = repository.getCharacters(page = state.value.nextPage?.toInt())
                emit(result.getOrThrow())
            }
        }

        override fun onFetchSuccess(data: PagedList<Character>): List<CharacterRender> {
            updateState { copy(nextPage = data.info.next.getPageNum()) }
            return data.results.map { it.toRender() }
        }

        override suspend fun onAction(action: CharactersAction) {
            TODO("Not yet implemented")
        }
    }

private fun String?.getPageNum(): String? {
    kotlin.runCatching {
        Uri.parse(this)
    }
        .onSuccess {
            return it.getQueryParameter("page")
        }

    return null
}

sealed class CharactersAction : UiAction {
    data class EditCharacter(val index: Int) : CharactersAction()
}

sealed class CharactersEffect : UiEffect {
    data class ShowMessage(val message: String) : CharactersEffect()
}

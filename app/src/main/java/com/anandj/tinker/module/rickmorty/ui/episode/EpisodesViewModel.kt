package com.anandj.tinker.module.rickmorty.ui.episode

import android.net.Uri
import com.anandj.tinker.core.ui.UiAction
import com.anandj.tinker.core.ui.UiEffect
import com.anandj.tinker.core.ui.list.PaginatedListState
import com.anandj.tinker.core.ui.list.PaginatedListViewModel
import com.anandj.tinker.module.rickmorty.data.RickMortyRepository
import com.anandj.tinker.module.rickmorty.data.api.Episode
import com.anandj.tinker.module.rickmorty.data.api.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel
    @Inject
    constructor(
        private val repository: RickMortyRepository,
    ) : PaginatedListViewModel<PagedList<Episode>, Episode, Unit, EpisodesAction, EpisodesEffect>(
            initialState = PaginatedListState(params = Unit),
        ) {
        override fun fetch(): Flow<PagedList<Episode>> {
            return flow {
                emit(repository.getEpisodes(page = state.value.nextPage?.toInt()))
            }
        }

        override fun onFetchSuccess(data: PagedList<Episode>): List<Episode> {
            updateState { copy(nextPage = data.info.next.getPageNum()) }
            return data.results
        }

        override suspend fun onAction(action: EpisodesAction) {
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

sealed class EpisodesAction : UiAction

sealed class EpisodesEffect : UiEffect {
    data class ShowMessage(val message: String) : EpisodesEffect()
}

package com.anandj.tinker.core.ui.list

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.anandj.tinker.core.ui.BaseViewModel
import com.anandj.tinker.core.ui.UiAction
import com.anandj.tinker.core.ui.UiEffect
import com.anandj.tinker.core.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

abstract class PaginatedListViewModel<FetchResultT, DomainT, ExtraT, ActionT : UiAction, EffectT : UiEffect>(
    initialState: PaginatedListState<ExtraT>,
) : BaseViewModel<PaginatedListState<ExtraT>, ActionT, EffectT>(initialState) {
    protected val mutableList = mutableStateListOf<DomainT>()
    val list: List<DomainT> = mutableList

    fun refresh() {
        load(false)
    }

    fun loadNextPage() {
        load(true)
    }

    private fun load(nextPage: Boolean) {
        if (state.value.loadingState == LoadingState.Refreshing || state.value.loadingState == LoadingState.Paging) return

        if (nextPage) {
            updateState { copy(loadingState = LoadingState.Paging) }
        } else {
            updateState { copy(loadingState = LoadingState.Refreshing) }
        }

        viewModelScope.launch {
            fetch()
                .catch { error ->
                    if (nextPage) {
                        updateState { copy(loadingState = LoadingState.PagingError) }
                    } else {
                        updateState { copy(loadingState = LoadingState.RefreshingError) }
                    }
                }
                .collect { result ->
                    updateState { copy(loadingState = LoadingState.Idle) }
                    val items = onFetchSuccess(result)

                    if (nextPage) {
                        mutableList.addAll(items)
                    } else {
                        mutableList.clear()
                        mutableList.addAll(items)
                    }
                }
        }
    }

    protected abstract fun fetch(): Flow<FetchResultT>

    protected abstract fun onFetchSuccess(data: FetchResultT): List<DomainT>
}

data class PaginatedListState<ExtraT>(
    val loadingState: LoadingState = LoadingState.Idle,
    val fetchParams: FetchParams = emptyMap(),
    val nextPage: String? = null,
    val extra: ExtraT,
) : UiState

enum class LoadingState {
    Idle,
    Refreshing,
    Paging,
    RefreshingError,
    PagingError,
}

typealias FetchParams = Map<String, Any?>

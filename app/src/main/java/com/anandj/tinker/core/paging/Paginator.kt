package com.anandj.tinker.core.paging

import com.anandj.tinker.core.ui.list.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface Paginator<ResultT, DomainT> {
    val state: StateFlow<PaginatorState>

    fun fetch(): Flow<ResultT>

    fun onFetchSuccess(
        nextPage: Boolean,
        result: ResultT,
    ): List<DomainT>

    fun loadNextPage()

    fun refresh()
}

object PaginatorFactory {
    fun <ResultT, DomainT> create(
        coroutineScope: CoroutineScope,
        fetch: () -> Flow<ResultT>,
        onFetchSuccess: (Boolean, ResultT) -> List<DomainT>,
    ): Paginator<ResultT, DomainT> =
        PaginatorImpl(
            coroutineScope = coroutineScope,
            _fetch = fetch,
            _onFetchSuccess = onFetchSuccess,
        )
}

internal class PaginatorImpl<ResultT, DomainT>(
    private val coroutineScope: CoroutineScope,
    private val _fetch: () -> Flow<ResultT>,
    private val _onFetchSuccess: (Boolean, ResultT) -> List<DomainT>,
) : Paginator<ResultT, DomainT> {
    private val _state = MutableStateFlow(PaginatorState())
    override val state: StateFlow<PaginatorState> = _state

    override fun fetch(): Flow<ResultT> = _fetch()

    override fun onFetchSuccess(
        nextPage: Boolean,
        result: ResultT,
    ): List<DomainT> = _onFetchSuccess(nextPage, result)

    override fun refresh() {
        load(false)
    }

    override fun loadNextPage() {
        load(true)
    }

    private fun load(nextPage: Boolean) {
        if (state.value.loadingState == LoadingState.Refreshing || state.value.loadingState == LoadingState.Paging) return

        if (nextPage) {
            updateState { copy(loadingState = LoadingState.Paging) }
        } else {
            updateState { copy(loadingState = LoadingState.Refreshing, nextPage = null) }
        }

        coroutineScope.launch {
            fetch()
                .catch { error ->
                    if (nextPage) {
                        updateState { copy(loadingState = LoadingState.PagingError) }
                    } else {
                        updateState { copy(loadingState = LoadingState.RefreshingError) }
                    }
                }.collect { result ->
                    updateState { copy(loadingState = LoadingState.Idle) }
                    onFetchSuccess(nextPage, result)
                }
        }
    }

    private fun updateState(block: PaginatorState.() -> PaginatorState) {
        _state.update(block)
    }
}

data class PaginatorState(
    val loadingState: LoadingState = LoadingState.Idle,
    val fetchParams: Map<String, Any?> = emptyMap(),
    val nextPage: String? = null,
)

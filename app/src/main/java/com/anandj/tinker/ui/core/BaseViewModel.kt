package com.anandj.tinker.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, in A : UiAction, E : UiEffect>(initialState: S) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state

    private val _effect by lazy { Channel<E>() }
    val effect: Flow<E> by lazy { _effect.receiveAsFlow() }

    protected fun updateState(state: S) {
        _state.update { state }
    }

    protected fun updateState(block: S.() -> S) {
        _state.update(block)
    }

    protected fun sendEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    fun sendAction(action: A) {
        viewModelScope.launch {
            onAction(action)
        }
    }

    abstract suspend fun onAction(action: A)
}

interface UiState

interface UiAction

interface UiEffect

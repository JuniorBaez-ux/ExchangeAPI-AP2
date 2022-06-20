package com.example.exchangeapiap2.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exchangeapiap2.data.ExchangeListState
import com.example.exchangeapiap2.repository.ExchangeRepository
import com.example.exchangeapiap2.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeRepository: ExchangeRepository
) : ViewModel() {

    private var _state = mutableStateOf(ExchangeListState())
    val state: State<ExchangeListState> = _state

    init {
        exchangeRepository.getExchanges().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = ExchangeListState(isLoading = true)
                }

                is Resource.Success -> {
                    _state.value = ExchangeListState(exchange = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = ExchangeListState(error = result.message ?: "Error desconocido")
                }
            }
        }.launchIn(viewModelScope)
    }

}
package com.example.exchangeapiap2.data

import com.example.exchangeapiap2.data.remote.dto.ExchangeDto

data class ExchangeListState(
    val isLoading: Boolean = false,
    val exchange: List<ExchangeDto> = emptyList(),
    val error: String = ""
)
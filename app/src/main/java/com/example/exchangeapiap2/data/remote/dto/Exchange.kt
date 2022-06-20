package com.example.exchangeapiap2.data.remote.dto

data class ExchangeDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val is_active: Boolean = false,
    val last_updated: String = ""
)

package com.example.exchangeapiap2.data.remote

import com.example.exchangeapiap2.data.remote.dto.ExchangeDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeApi {
    @GET("/api/exchanges/")
    suspend fun getExchange(): List<ExchangeDto>

    @GET("/v1/exchanges/{exchangeId}")
    suspend fun getExchange(@Path("coinId") coinId: String): ExchangeDto
}
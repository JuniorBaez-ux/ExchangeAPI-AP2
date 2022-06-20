package com.example.exchangeapiap2.repository

import com.example.exchangeapiap2.data.remote.ExchangeApi
import com.example.exchangeapiap2.data.remote.dto.ExchangeDto
import com.example.exchangeapiap2.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ExchangeRepository @Inject constructor(
    private val api: ExchangeApi
) {
    fun getExchanges(): Flow<Resource<List<ExchangeDto>>> = flow {
        try {
            emit(Resource.Loading()) //indicar que estamos cargando

            val exchanges = api.getExchange() //descarga las monedas de internet, se supone quedemora algo

            emit(Resource.Success(exchanges)) //indicar que se cargo correctamente y pasarle las monedas
        } catch (e: HttpException) {
            //error general HTTP
            emit(Resource.Error(e.message ?: "Error HTTP GENERAL"))
        } catch (e: IOException) {
            //debe verificar tu conexion a internet
            emit(Resource.Error(e.message ?: "verificar tu conexion a internet"))
        }
    }
}
package com.example.exchangeapiap2.di

import com.example.exchangeapiap2.data.remote.ExchangeApi
import com.example.exchangeapiap2.repository.ExchangeRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideCoinApi(moshi: Moshi): ExchangeApi {
        return Retrofit.Builder()
            .baseUrl("https://api.coinlore.net")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ExchangeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoinRepository(coinApi: ExchangeApi): ExchangeRepository {
        return ExchangeRepository(coinApi)
    }
}


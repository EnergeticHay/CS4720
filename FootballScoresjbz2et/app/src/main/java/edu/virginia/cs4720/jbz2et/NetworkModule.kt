package edu.virginia.cs4720.jbz2et

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {
    private val json = Json { ignoreUnknownKeys = true }

    val api: ScoreboardApi = Retrofit.Builder()
        .baseUrl("https://ncaa-api.henrygd.me/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ScoreboardApi::class.java)
}
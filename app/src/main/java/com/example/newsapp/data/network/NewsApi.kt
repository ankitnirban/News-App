package com.example.newsapp.data.network

import com.example.newsapp.data.network.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET


interface NewsApi {
    @GET("top-headlines?language=en")
    suspend fun getBreakingNews(): Response<NewsResponse>
}
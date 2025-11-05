package com.example.newsapp.data.network

import retrofit2.Response
import retrofit2.http.GET


interface NewsApi {
    @GET("top-headlines?language=en")
    suspend fun getBreakingNews(): Response<NewsResponse>
}

data class NewsResponse(
    val articles: List<NewsArticle>
)
data class NewsArticle(
    val title: String
)
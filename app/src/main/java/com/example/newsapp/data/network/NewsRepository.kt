package com.example.newsapp.data.network

import javax.inject.Inject

class NewsRepository @Inject constructor(
    val newsApi: NewsApi
) {
    suspend fun getBreakingNews(): List<NewsArticle> {
        val newsResponse = newsApi.getBreakingNews()
        if (newsResponse.isSuccessful) {
            return newsResponse.body()?.articles ?: emptyList()
        } else {
            return emptyList()
        }
    }
}
package com.example.newsapp.data.network.model

data class NewsResponse(
    val articles: List<NewsArticleResponse>,
)

data class NewsArticleResponse(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val content: String?,
    val source: NewsSourceResponse?,
)

data class NewsSourceResponse(
    val id: String,
    val name: String?,
)

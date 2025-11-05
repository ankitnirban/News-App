package com.example.newsapp.model

data class NewsResponse(
    val articles: List<NewsArticle>
)

data class NewsArticle(
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val content: String?,
    val source: NewsSource?
)

data class NewsSource(
    val id: String,
    val name: String?
)

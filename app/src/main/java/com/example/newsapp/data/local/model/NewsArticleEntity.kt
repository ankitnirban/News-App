package com.example.newsapp.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val content: String?,
    @Embedded val source: NewsSourceEntity?,
    val saved: Boolean = false,
)

data class NewsSourceEntity(
    val id: String?,
    val name: String?,
)

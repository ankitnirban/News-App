package com.example.newsapp.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val content: String?,
    @Embedded val source: NewsSourceEntity?
)

data class NewsSourceEntity(
    val sourceId: String?,
    val sourceName: String?
)

package com.example.newsapp.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "news_articles")
data class NewsArticleEntity(
    val title: String,
    val description: String?,
    @PrimaryKey val url: String,
    val urlToImage: String?,
    val content: String?,
    @Embedded val source: NewsSourceEntity?,
    val saved: Boolean = false,
    val savedAt: Date?
)

data class NewsSourceEntity(
    val id: String?,
    val name: String?,
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

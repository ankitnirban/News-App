package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.data.local.model.NewsArticleEntity

@Database(
    entities = [NewsArticleEntity::class],
    version = 4,
    exportSchema = false
)
abstract class NewsDatabase: RoomDatabase() {
    abstract fun newsArticleDao(): NewsArticleDao
}
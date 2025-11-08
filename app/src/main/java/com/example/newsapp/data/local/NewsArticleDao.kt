package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.model.NewsArticleEntity

@Dao
interface NewsArticleDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertArticle(article: NewsArticleEntity)

    @Query("SELECT * FROM news_articles")
    suspend fun getAllArticles(): List<NewsArticleEntity>

}
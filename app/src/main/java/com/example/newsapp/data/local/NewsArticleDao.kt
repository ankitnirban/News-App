package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.model.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsArticleDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertArticle(article: NewsArticleEntity)

    @Query("UPDATE news_articles SET saved = 1 WHERE url = :url")
    suspend fun saveNewsArticle(url: String)

    @Query("UPDATE news_articles SET saved = 0 WHERE url = :url")
    suspend fun unsaveNewsArticle(url: String)

    @Query("SELECT * FROM news_articles ORDER BY url DESC")
    fun getAllArticles(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles ORDER BY url DESC")
    suspend fun getAllArticlesOnce(): List<NewsArticleEntity>

    @Query("DELETE FROM news_articles")
    suspend fun clearAllArticles()

    @Query("SELECT * FROM news_articles WHERE saved = 1")
    fun getSavedArticles(): Flow<List<NewsArticleEntity>>

    @Query("SELECT url FROM news_articles WHERE saved = 1")
    suspend fun getSavedArticleUrlsOnce(): List<String>

    @Query("DELETE FROM news_articles WHERE saved = 0")
    fun clearUnsavedArticles()
}

package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.model.NewsArticleEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface NewsArticleDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)

    @Query("UPDATE news_articles SET saved = 1, savedAt = :date WHERE url = :url")
    suspend fun saveNewsArticle(url: String, date: Date)

    @Query("UPDATE news_articles SET saved = 0, savedAt = NULL WHERE url = :url")
    suspend fun unsaveNewsArticle(url: String)

    @Query("SELECT * FROM news_articles")
    fun getAllArticles(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): NewsArticleEntity?

    @Query("SELECT * FROM news_articles WHERE saved = 1 ORDER BY savedAt DESC")
    fun getSavedArticles(): Flow<List<NewsArticleEntity>>

    @Query("SELECT url FROM news_articles WHERE saved = 1")
    suspend fun getSavedArticleUrlsOnce(): List<String>

    @Query("DELETE FROM news_articles WHERE saved = 0")
    suspend fun clearUnsavedArticles()
}

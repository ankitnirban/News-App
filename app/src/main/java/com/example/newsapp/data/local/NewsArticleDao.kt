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

    @Query("UPDATE news_articles SET saved = 1 WHERE title = :title")
    suspend fun saveNewsArticle(title: String)

    @Query("UPDATE news_articles SET saved = 0 WHERE title = :title")
    suspend fun unsaveNewsArticle(title: String)

    @Query("SELECT * FROM news_articles ORDER BY id DESC")
    fun getAllArticles(): Flow<List<NewsArticleEntity>>

    @Query("SELECT * FROM news_articles ORDER BY id DESC")
    suspend fun getAllArticlesOnce(): List<NewsArticleEntity>

    @Query("DELETE FROM news_articles")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM news_articles WHERE saved = 1")
    fun getSavedArticles(): Flow<List<NewsArticleEntity>>

}
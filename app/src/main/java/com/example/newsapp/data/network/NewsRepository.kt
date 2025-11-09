package com.example.newsapp.data.network

import android.util.Log
import com.example.newsapp.data.local.NewsArticleDao
import com.example.newsapp.data.local.model.NewsArticleEntity
import com.example.newsapp.data.local.model.NewsSourceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepository @Inject constructor(
    val newsApi: NewsApi,
    val newsArticleDao: NewsArticleDao
) {
    /**
     * Returns a Flow that observes the database for breaking news articles.
     * This provides offline-first behavior - UI immediately shows cached data.
     */

    suspend fun saveNewsArticle(title: String) {
        newsArticleDao.saveNewsArticle(title)
    }

    suspend fun unsaveNewsArticle(title: String) {
        newsArticleDao.unsaveNewsArticle(title)
    }

    fun getBreakingNews(): Flow<List<NewsArticle>> {
        return newsArticleDao.getAllArticles().map { entities ->
            entities.map { entity ->
                NewsArticle(
                    title = entity.title,
                    description = entity.description,
                    url = entity.url,
                    urlToImage = entity.urlToImage,
                    content = entity.content,
                    source = NewsSource(
                        id = entity.source?.id,
                        name = entity.source?.name
                    ),
                    saved = entity.saved
                )
            }
        }
    }

    fun getSavedNewsArticles(): Flow<List<NewsArticle>> {
        return newsArticleDao.getSavedArticles().map { entities ->
            entities.map { entity ->
                NewsArticle(
                    title = entity.title,
                    description = entity.description,
                    url = entity.url,
                    urlToImage = entity.urlToImage,
                    content = entity.content,
                    source = NewsSource(
                        id = entity.source?.id,
                        name = entity.source?.name
                    ),
                    saved = entity.saved
                )
            }
        }
    }

    /**
     * Refreshes breaking news from network and updates the database.
     * Should be called in the background - database Flow will automatically update UI.
     */
    suspend fun refreshBreakingNews() {
        try {
            val newsArticlesFromNetwork = getBreakingNewsFromNetwork()
            if (newsArticlesFromNetwork.isNotEmpty()) {
                val newsArticlesFromDb = getBreakingNewsFromDbOnce()
                if (!areArticlesEqual(newsArticlesFromNetwork, newsArticlesFromDb)) {
                    saveBreakingNewsToDb(newsArticlesFromNetwork)
                    Log.d("NewsRepository", "Breaking news refreshed from network")
                }
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Error refreshing breaking news", e)
            // Fail silently - offline data will still be shown
        }
    }

    private fun areArticlesEqual(networkArticles: List<NewsArticle>, dbArticles: List<NewsArticle>): Boolean {
        if (networkArticles.size != dbArticles.size) {
            return false
        }
        
        // Compare articles by their content (ignoring ID since network articles have id=null)
        val networkArticlesByUrl = networkArticles.associateBy { it.url }
        val dbArticlesByUrl = dbArticles.associateBy { it.url }
        
        if (networkArticlesByUrl.keys != dbArticlesByUrl.keys) {
            return false
        }
        
        // Compare each article's content (excluding ID)
        return networkArticlesByUrl.all { (url, networkArticle) ->
            val dbArticle = dbArticlesByUrl[url]
            dbArticle != null && articlesContentEqual(networkArticle, dbArticle)
        }
    }

    private fun articlesContentEqual(article1: NewsArticle, article2: NewsArticle): Boolean {
        return article1.title == article2.title &&
                article1.description == article2.description &&
                article1.url == article2.url &&
                article1.urlToImage == article2.urlToImage &&
                article1.content == article2.content &&
                article1.source?.id == article2.source?.id &&
                article1.source?.name == article2.source?.name
    }

    private suspend fun getBreakingNewsFromNetwork(): List<NewsArticle> {
        val newsArticleResponse = newsApi.getBreakingNews()
        return if (newsArticleResponse.isSuccessful) {
            newsArticleResponse.body()?.articles?.map {
                NewsArticle(
                    title = it.title,
                    description = it.description,
                    url = it.url,
                    urlToImage = it.urlToImage,
                    content = it.content,
                    source = NewsSource(
                        id = it.source?.id,
                        name = it.source?.name
                    )
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    private suspend fun getBreakingNewsFromDbOnce(): List<NewsArticle> {
        val newsArticleEntities = newsArticleDao.getAllArticlesOnce()
        return newsArticleEntities.map {
            NewsArticle(
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                content = it.content,
                source = NewsSource(
                    id = it.source?.id,
                    name = it.source?.name
                ),
                saved = it.saved
            )
        }
    }

    private suspend fun saveBreakingNewsToDb(newsArticles: List<NewsArticle>) {
        // Clear existing articles before inserting new ones to prevent duplicates
        // Breaking news is a fresh list that replaces the previous one
        
        val newsArticleEntities = newsArticles.map {
            NewsArticleEntity(
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                content = it.content,
                source = NewsSourceEntity(
                    id = it.source?.id,
                    name = it.source?.name
                )
            )
        }
        newsArticleDao.insertArticles(newsArticleEntities)
        Log.d("NewsRepository", "News articles saved to database")
    }
}

data class NewsArticle(
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val content: String?,
    val source: NewsSource?,
    val saved: Boolean = false
)

data class NewsSource(
    val id: String?,
    val name: String?
)
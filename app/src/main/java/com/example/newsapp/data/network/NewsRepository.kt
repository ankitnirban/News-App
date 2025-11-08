package com.example.newsapp.data.network

import android.util.Log
import com.example.newsapp.data.local.NewsArticleDao
import com.example.newsapp.data.local.model.NewsArticleEntity
import com.example.newsapp.data.local.model.NewsSourceEntity
import javax.inject.Inject

class NewsRepository @Inject constructor(
    val newsApi: NewsApi,
    val newsArticleDao: NewsArticleDao
) {
    suspend fun getBreakingNews(): List<NewsArticle> {
        val newsArticlesFromDb = getBreakingNewsFromDb()
        val newsArticlesFromNetwork = getBreakingNewsFromNetwork()
        if (newsArticlesFromNetwork.isNotEmpty() && !areArticlesEqual(newsArticlesFromNetwork, newsArticlesFromDb)) {
            saveBreakingNewsToDb(newsArticlesFromNetwork)
        }
        return getBreakingNewsFromDb()
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

    suspend fun getBreakingNewsFromNetwork(): List<NewsArticle> {
        val newsArticleResponse = newsApi.getBreakingNews()
        return if (newsArticleResponse.isSuccessful) {
            newsArticleResponse.body()?.articles?.map {
                NewsArticle(
                    id = null,
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

    suspend fun getBreakingNewsFromDb(): List<NewsArticle> {
        val newsArticleEntities = newsArticleDao.getAllArticles()
        return newsArticleEntities.map {
            NewsArticle(
                id = it.id,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                content = it.content,
                source = NewsSource(
                    id = it.source?.sourceId,
                    name = it.source?.sourceName
                )
            )
        }
    }

    suspend fun saveBreakingNewsToDb(newsArticles: List<NewsArticle>) {
        val newsArticleEntities = newsArticles.map {
            NewsArticleEntity(
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                content = it.content,
                source = NewsSourceEntity(
                    sourceId = it.source?.id,
                    sourceName = it.source?.name
                )
            )
        }
        newsArticleDao.insertArticles(newsArticleEntities)
        Log.d("NewsRepository", "News articles saved to database")
    }
}

data class NewsArticle(
    val id: Long?,
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val content: String?,
    val source: NewsSource?
)

data class NewsSource(
    val id: String?,
    val name: String?
)
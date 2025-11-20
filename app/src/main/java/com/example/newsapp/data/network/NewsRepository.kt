package com.example.newsapp.data.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.newsapp.data.local.NewsArticleDao
import com.example.newsapp.data.local.model.NewsArticleEntity
import com.example.newsapp.data.local.model.NewsSourceEntity
import com.example.newsapp.data.network.model.NewsArticleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.Date
import javax.inject.Inject

class NewsRepository
    @Inject
    constructor(
        val newsApi: NewsApi,
        val newsArticleDao: NewsArticleDao,
    ) {

        @RequiresApi(Build.VERSION_CODES.O)
        suspend fun saveNewsArticle(url: String) {
            newsArticleDao.saveNewsArticle(url, Date.from(Instant.now()))
        }

        suspend fun unsaveNewsArticle(url: String) {
            newsArticleDao.unsaveNewsArticle(url)
        }

        fun getBreakingNews(): Flow<List<NewsArticle>> =
            newsArticleDao.getAllArticles().map { entities ->
                entities.map { entity -> mapEntityToNewsArticle(entity) }
            }

        suspend fun searchNews(query: String): List<NewsArticle> {
            val searchNewsResponse = newsApi.searchNews(query)
            return if (searchNewsResponse.isSuccessful) {
                searchNewsResponse.body()?.articles?.map { response ->
                    mapResponseToNewsArticle(response)
                } ?: emptyList()
            } else {
                emptyList()
            }
        }

        fun getSavedNewsArticles(): Flow<List<NewsArticle>> =
            newsArticleDao.getSavedArticles().map { entities ->
                entities.map { entity -> mapEntityToNewsArticle(entity) }
            }

        suspend fun refreshBreakingNews() {
            try {
                val newsArticlesFromNetwork = getBreakingNewsFromNetwork()
                if (newsArticlesFromNetwork.isNotEmpty()) {
                    val savedArticleUrls = newsArticleDao.getSavedArticleUrlsOnce()
                    val newsArticleEntities = newsArticlesFromNetwork.map { article ->
                        mapNewsArticleToEntity(article, savedArticleUrls)
                    }
                    newsArticleDao.clearUnsavedArticles()
                    newsArticleDao.insertArticles(newsArticleEntities)
                }
            } catch (e: Exception) {
                Log.e("NewsRepository", "Error refreshing breaking news", e)
            }
        }

        private suspend fun getBreakingNewsFromNetwork(): List<NewsArticle> {
            val newsArticleResponse = newsApi.getBreakingNews()
            return if (newsArticleResponse.isSuccessful) {
                newsArticleResponse.body()?.articles?.map { response ->
                    mapResponseToNewsArticle(response)
                } ?: emptyList()
            } else {
                emptyList()
            }
        }

        private suspend fun mapNewsArticleToEntity(
            article: NewsArticle,
            savedArticleUrls: List<String>,
        ): NewsArticleEntity {
            val isArticleSaved = savedArticleUrls.contains(article.url)
            val savedAt =
                if (!isArticleSaved) {
                    null
                } else {
                    newsArticleDao.getArticleByUrl(article.url)!!.savedAt
                }
            return NewsArticleEntity(
                title = article.title,
                description = article.description,
                url = article.url,
                urlToImage = article.urlToImage,
                content = article.content,
                source =
                    article.source?.let {
                        NewsSourceEntity(
                            id = it.id,
                            name = it.name,
                        )
                    },
                saved = isArticleSaved,
                savedAt = savedAt,
            )
        }

        private fun mapEntityToNewsArticle(entity: NewsArticleEntity): NewsArticle {
            return NewsArticle(
                title = entity.title,
                description = entity.description,
                url = entity.url,
                urlToImage = entity.urlToImage,
                content = entity.content,
                source =
                    NewsSource(
                        id = entity.source?.id,
                        name = entity.source?.name,
                    ),
                saved = entity.saved,
            )
        }

        private fun mapResponseToNewsArticle(response: NewsArticleResponse): NewsArticle {
            return NewsArticle(
                title = response.title,
                description = response.description,
                url = response.url,
                urlToImage = response.urlToImage,
                content = response.content,
                source =
                    NewsSource(
                        id = response.source?.id,
                        name = response.source?.name,
                    ),
            )
        }
    }

data class NewsArticle(
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val content: String?,
    val source: NewsSource?,
    val saved: Boolean = false,
)

data class NewsSource(
    val id: String?,
    val name: String?,
)

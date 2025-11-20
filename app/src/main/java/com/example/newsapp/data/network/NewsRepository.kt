package com.example.newsapp.data.network

import android.util.Log
import com.example.newsapp.data.local.NewsArticleDao
import com.example.newsapp.data.local.model.NewsArticleEntity
import com.example.newsapp.data.local.model.NewsSourceEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class NewsRepository
    @Inject
    constructor(
        val newsApi: NewsApi,
        val newsArticleDao: NewsArticleDao,
    ) {

        suspend fun saveNewsArticle(url: String) {
            newsArticleDao.saveNewsArticle(url)
        }

        suspend fun unsaveNewsArticle(url: String) {
            newsArticleDao.unsaveNewsArticle(url)
        }

        fun getBreakingNews(): Flow<List<NewsArticle>> =
            newsArticleDao.getAllArticles().map { entities ->
                entities.map { entity ->
                    NewsArticle(
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
            }

        suspend fun searchNews(query: String): List<NewsArticle> {
            val searchNewsResponse = newsApi.searchNews(query)
            return if (searchNewsResponse.isSuccessful) {
                searchNewsResponse.body()?.articles?.map {
                    NewsArticle(
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        urlToImage = it.urlToImage,
                        content = it.content,
                        source =
                            NewsSource(
                                id = it.source?.id,
                                name = it.source?.name,
                            ),
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
        }

        fun getSavedNewsArticles(): Flow<List<NewsArticle>> =
            newsArticleDao.getSavedArticles().map { entities ->
                entities.map { entity ->
                    NewsArticle(
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
            }

        suspend fun refreshBreakingNews() {
            try {
                val newsArticlesFromNetwork = getBreakingNewsFromNetwork()
                if (newsArticlesFromNetwork.isNotEmpty()) {
                    val savedArticleUrls = newsArticleDao.getSavedArticleUrlsOnce()
                    val newsArticleEntities = newsArticlesFromNetwork.map { articleDto ->
                        NewsArticleEntity(
                            title = articleDto.title,
                            description = articleDto.description,
                            url = articleDto.url,
                            urlToImage = articleDto.urlToImage,
                            content = articleDto.content,
                            source = articleDto.source?.let {
                                NewsSourceEntity(
                                    id = it.id,
                                    name = it.name
                                )
                            },
                            saved = savedArticleUrls.contains(articleDto.url)
                        )
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
                newsArticleResponse.body()?.articles?.map {
                    NewsArticle(
                        title = it.title,
                        description = it.description,
                        url = it.url,
                        urlToImage = it.urlToImage,
                        content = it.content,
                        source =
                            NewsSource(
                                id = it.source?.id,
                                name = it.source?.name,
                            ),
                    )
                } ?: emptyList()
            } else {
                emptyList()
            }
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

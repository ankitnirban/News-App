package com.example.newsapp.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.network.NewsArticle
import com.example.newsapp.data.network.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
): ViewModel() {

    /**
     * Observes breaking news articles from the database.
     * Automatically updates when database changes (offline-first approach).
     */
    val breakingNewsArticles: StateFlow<List<NewsArticle>> = newsRepository.getBreakingNews()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val savedNewsArticles: StateFlow<List<NewsArticle>> = newsRepository.getSavedNewsArticles()
        .stateIn(
            scope = viewModelScope,
            started = WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Refreshes breaking news from network in the background.
     * Database Flow will automatically update UI when new data arrives.
     */
    fun refreshBreakingNews() {
        viewModelScope.launch {
            newsRepository.refreshBreakingNews()
        }
    }

    fun saveNewsArticle(title: String) {
        viewModelScope.launch { newsRepository.saveNewsArticle(title) }
    }

    fun unsaveNewsArticle(title: String) {
        viewModelScope.launch { newsRepository.unsaveNewsArticle(title) }
    }
}
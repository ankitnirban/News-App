package com.example.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.network.NewsArticle
import com.example.newsapp.data.network.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel
    @Inject
    constructor(
        private val newsRepository: NewsRepository,
    ) : ViewModel() {
        /**
         * Observes breaking news articles from the database.
         * Automatically updates when database changes (offline-first approach).
         */
        val breakingNewsArticles: StateFlow<List<NewsArticle>> =
            newsRepository
                .getBreakingNews()
                .stateIn(
                    scope = viewModelScope,
                    started = WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        val savedNewsArticles: StateFlow<List<NewsArticle>> =
            newsRepository
                .getSavedNewsArticles()
                .stateIn(
                    scope = viewModelScope,
                    started = WhileSubscribed(5000),
                    initialValue = emptyList(),
                )

        private val _searchResults: MutableStateFlow<List<NewsArticle>> = MutableStateFlow(emptyList())
        val searchResults = _searchResults.asStateFlow()

        fun refreshBreakingNews() {
            viewModelScope.launch {
                newsRepository.refreshBreakingNews()
            }
        }

        fun saveNewsArticle(url: String) {
            viewModelScope.launch { newsRepository.saveNewsArticle(url) }
        }

        fun unsaveNewsArticle(url: String) {
            viewModelScope.launch { newsRepository.unsaveNewsArticle(url) }
        }

        fun searchNews(query: String) {
            viewModelScope.launch {
                _searchResults.update { newsRepository.searchNews(query) }
            }
        }
    }

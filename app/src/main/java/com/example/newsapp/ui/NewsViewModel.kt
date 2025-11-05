package com.example.newsapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.network.NewsArticle
import com.example.newsapp.data.network.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
): ViewModel() {

    private val _breakingNewsArticles: MutableStateFlow<List<NewsArticle>> = MutableStateFlow(emptyList())
    val breakingNewsArticles: StateFlow<List<NewsArticle>> = _breakingNewsArticles.asStateFlow()

    fun getBreakingNews() {
        viewModelScope.launch {
            _breakingNewsArticles.update { newsRepository.getBreakingNews() }
        }
    }
}
package com.example.newsapp.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsapp.ui.component.NewsArticleItem
import com.example.newsapp.ui.NewsViewModel

@Composable
fun BreakingNewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    navigateToNewsDetailsScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Collect StateFlow - automatically updates when database changes (offline-first)
    val breakingNewsArticles = newsViewModel.breakingNewsArticles.collectAsStateWithLifecycle()

    // Refresh from network on initial load (runs in background)
    // Cached data is shown immediately, then updates when network data arrives
    LaunchedEffect(Unit) {
        newsViewModel.refreshBreakingNews()
    }

    LazyColumn(modifier = modifier) {
        items(breakingNewsArticles.value) { article ->
            NewsArticleItem(
                article = article,
                navigateToNewsDetailsScreen = navigateToNewsDetailsScreen,
                saveNewsArticle = { title -> newsViewModel.saveNewsArticle(title) },
                unsaveNewsArticle = { title -> newsViewModel.unsaveNewsArticle(title) }
            )
        }
    }
}
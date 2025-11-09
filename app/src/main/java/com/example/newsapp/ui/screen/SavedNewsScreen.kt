package com.example.newsapp.ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.ui.component.NewsArticleItem

@Composable
fun SavedNewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    navigateToNewsDetailsScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val savedNewsArticles = newsViewModel.savedNewsArticles.collectAsStateWithLifecycle()

    LazyColumn(modifier = modifier) {
        items(savedNewsArticles.value) { article ->
            NewsArticleItem(
                article = article,
                navigateToNewsDetailsScreen = navigateToNewsDetailsScreen,
                unsaveNewsArticle = { title -> newsViewModel.unsaveNewsArticle(title) }
            )
        }
    }
}


package com.example.newsapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BreakingNewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    navigateToNewsDetailsScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val breakingNewsArticles = newsViewModel.breakingNewsArticles.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        newsViewModel.getBreakingNews()
    }

    LazyColumn(modifier = modifier) {
        items(breakingNewsArticles.value) { article ->
            NewsArticleItem(
                article = article,
                navigateToNewsDetailsScreen = navigateToNewsDetailsScreen,
            )
        }
    }
}
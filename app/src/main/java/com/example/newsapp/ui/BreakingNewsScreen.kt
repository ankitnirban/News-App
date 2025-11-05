package com.example.newsapp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BreakingNewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val breakingNewsArticles = newsViewModel.breakingNewsArticles.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        newsViewModel.getBreakingNews()
    }

    LazyColumn(modifier = modifier) {
        items(breakingNewsArticles.value.size) { index ->
            Text(text = breakingNewsArticles.value[index].title)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
package com.example.newsapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.ui.component.NewsArticleItem
import com.example.newsapp.ui.component.SearchBar
import kotlinx.coroutines.delay

@Composable
fun SearchNewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    navigateToNewsDetailsScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = newsViewModel.searchResults.collectAsStateWithLifecycle()

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            delay(300)
            newsViewModel.searchNews(searchQuery)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(searchResults.value.size) { index ->
                NewsArticleItem(
                    article = searchResults.value[index],
                    saveNewsArticle = { url -> newsViewModel.saveNewsArticle(url) },
                    unsaveNewsArticle = { url -> newsViewModel.unsaveNewsArticle(url) },
                    navigateToNewsDetailsScreen = navigateToNewsDetailsScreen,
                )
            }
        }
    }
}

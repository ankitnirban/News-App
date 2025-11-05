package com.example.newsapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NewsMainScreen(
    newsViewModel: NewsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                modifier = Modifier.height(200.dp)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.BreakingNews,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Destination.BreakingNews> {
                BreakingNewsScreen(
                    newsViewModel = newsViewModel,
                    modifier = modifier
                )
            }
            composable<Destination.SavedNews> {
                SavedNewsScreen(
                    newsViewModel = newsViewModel,
                    modifier = modifier
                )
            }
            composable<Destination.SearchNews> {
                SearchNewsScreen(
                    newsViewModel = newsViewModel,
                    modifier = modifier
                )
            }
        }
    }
}

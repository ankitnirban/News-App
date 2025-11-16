package com.example.newsapp.ui

import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object BreakingNews : Destination()

    @Serializable
    data object SavedNews : Destination()

    @Serializable
    data object SearchNews : Destination()

    @Serializable
    data class NewsDetails(
        val webUrl: String,
    ) : Destination()
}

fun navigateToDestination(
    navController: NavHostController,
    destination: Destination,
) {
    navController.navigate(destination) {
        popUpTo<Destination.BreakingNews> {
            inclusive = false
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

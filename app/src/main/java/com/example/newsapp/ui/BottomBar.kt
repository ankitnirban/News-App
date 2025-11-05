package com.example.newsapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.newsapp.R
import androidx.navigation.NavHostController

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    BottomAppBar {
        Destination::class.sealedSubclasses.forEach { it ->
            val route = it.qualifiedName
            it.objectInstance?.let { destination ->
                BottomBarItem(
                    iconResId = R.drawable.ic_launcher_background,
                    text = when(destination) {
                        is Destination.BreakingNews -> "Breaking"
                        is Destination.SavedNews -> "Saved"
                        is Destination.SearchNews -> "Search"
                    },
                    tabSelected = currentBackStackEntry?.destination?.route == route,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navigateToDestination(navController, destination)
                        }
                )
            }
        }
    }
}

@Composable
fun BottomBarItem(
    iconResId: Int,
    text: String,
    tabSelected: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontWeight = if (tabSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

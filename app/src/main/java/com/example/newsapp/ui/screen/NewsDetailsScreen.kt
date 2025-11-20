package com.example.newsapp.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NewsDetailsScreen(
    webUrl: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context -> createWebView(context) },
        update = { webView -> webView.loadUrl(webUrl) },
    )
}

private fun createWebView(context: Context): WebView {
    return WebView(context).apply {
        settings.javaScriptEnabled = true
        webViewClient = WebViewClient()
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
    }
}

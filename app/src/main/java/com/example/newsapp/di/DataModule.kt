package com.example.newsapp.di

import android.content.Context
import androidx.room.Room
import com.example.newsapp.data.local.NewsArticleDao
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.network.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    companion object {
        private const val API_KEY = "d93f9f4d733749a78188b21d62a1b79d"
        private const val BASE_URL = "https://newsapi.org/v2/"
    }

    @Provides
    @Singleton
    fun newsApi(): NewsApi {
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient)
        return retrofit.create(NewsApi::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient
            .Builder()
            .addInterceptor(createApiKeyInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private fun createApiKeyInterceptor(): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest =
                originalRequest
                    .newBuilder()
                    .header("X-Api-Key", API_KEY)
                    .build()
            chain.proceed(newRequest)
        }
    }

    private fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun newsDatabase(
        @ApplicationContext context: Context,
    ): NewsDatabase {
        val database =
            Room
                .databaseBuilder(
                    context,
                    NewsDatabase::class.java,
                    "news_database",
                ).fallbackToDestructiveMigration()
                .build()
        return database
    }

    @Provides
    @Singleton
    fun newsArticleDao(newsDatabase: NewsDatabase): NewsArticleDao = newsDatabase.newsArticleDao()
}

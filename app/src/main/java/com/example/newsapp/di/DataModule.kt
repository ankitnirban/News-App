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
    @Provides
    @Singleton
    fun newsApi(): NewsApi {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequest =
                        originalRequest
                            .newBuilder()
                            .header("X-Api-Key", "d93f9f4d733749a78188b21d62a1b79d")
                            .build()
                    chain.proceed(newRequest)
                }.addInterceptor(loggingInterceptor)
                .build()
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl("https://newsapi.org/v2/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(NewsApi::class.java)
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

package com.example.networkcalls.di

import androidx.viewbinding.BuildConfig
import com.example.networkcalls.network.api.CarsApiService
import com.example.networkcalls.network.api.helpers.CarsApiHelper
import com.example.networkcalls.network.api.helpers.CarsApiHelperImpl
import com.example.networkcalls.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = Constants.carsServiceBaseUrl

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        } else {
            OkHttpClient
                .Builder()
                .build()
        }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideCarsApiService(retrofit: Retrofit) = retrofit.create(CarsApiService::class.java)

    @Provides
    @Singleton
    fun provideCarsApiHelper(carsApiHelper: CarsApiHelperImpl): CarsApiHelper = carsApiHelper
}
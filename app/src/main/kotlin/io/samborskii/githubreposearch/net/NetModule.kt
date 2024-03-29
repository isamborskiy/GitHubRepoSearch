package io.samborskii.githubreposearch.net

import dagger.Module
import dagger.Provides
import io.samborskii.githubreposearch.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
open class NetModule {

    @Singleton
    @Provides
    open fun okHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else
            HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Named("apiHost")
    @Singleton
    @Provides
    open fun apiHost(): String = API_HOST
}

package io.samborskii.githubreposearch.api

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import io.samborskii.githubreposearch.api.impl.GitHubClientImpl
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
open class ApiModule {

    @Singleton
    @Provides
    open fun client(
        @Named("apiHost") host: String,
        okHttpClient: OkHttpClient,
        objectMapper: ObjectMapper
    ): GitHubClient = GitHubClientImpl(host, okHttpClient, objectMapper)
}

package io.samborskii.githubreposearch

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule(private val application: GitHubRepoSearchApplication) {

    @Singleton
    @Provides
    open fun context(): Context = application

    @Singleton
    @Provides
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()
}

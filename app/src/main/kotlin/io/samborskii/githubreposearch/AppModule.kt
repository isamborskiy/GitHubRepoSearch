package io.samborskii.githubreposearch

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

const val PAGE_SIZE = 5

@Module
open class AppModule(private val application: GitHubRepoSearchApplication) {

    @Singleton
    @Provides
    open fun context(): Context = application

    @Singleton
    @Provides
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()

    @Named("pageSize")
    @Singleton
    @Provides
    open fun pageSize(): Int = PAGE_SIZE
}

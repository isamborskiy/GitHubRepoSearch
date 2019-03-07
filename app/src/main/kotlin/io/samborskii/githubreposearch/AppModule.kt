package io.samborskii.githubreposearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

const val PAGE_SIZE = 10

@Module
open class AppModule {

    @Singleton
    @Provides
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()

    @Named("pageSize")
    @Singleton
    @Provides
    open fun pageSize(): Int = PAGE_SIZE
}

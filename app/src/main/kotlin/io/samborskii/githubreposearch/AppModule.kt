package io.samborskii.githubreposearch

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class AppModule {

    @Singleton
    @Provides
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()
}

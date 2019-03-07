package io.samborskii.githubreposearch.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.samborskii.githubreposearch.api.GitHubClient
import io.samborskii.githubreposearch.api.SearchParams
import io.samborskii.githubreposearch.api.impl.GitHubClientImpl
import io.samborskii.githubreposearch.net.API_HOST
import okhttp3.OkHttpClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import retrofit2.HttpException

@Tag("integration")
class GitHubAPITest {

    private lateinit var client: GitHubClient

    @BeforeEach
    fun setup() {
        val okHttpClient = OkHttpClient()
        client = GitHubClientImpl(API_HOST, okHttpClient, jacksonObjectMapper())
    }

    @Test
    fun `make _nus bus_ query`() {
        val searchParams = SearchParams("nus bus", 1, 100)

        client.searchRepositories(searchParams)
            .test()
            .assertValue { it.totalCount > 0 && it.items.isNotEmpty() }
    }

    @Test
    fun `make empty query`() {
        val searchParams = SearchParams("", 1, 100)

        client.searchRepositories(searchParams)
            .test()
            .assertError { it is HttpException && it.code() == 422 }
    }
}

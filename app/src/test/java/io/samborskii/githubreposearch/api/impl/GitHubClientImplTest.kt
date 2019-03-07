package io.samborskii.githubreposearch.api.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.samborskii.githubreposearch.api.BaseMockWebServerTest
import io.samborskii.githubreposearch.api.GitHubClient
import io.samborskii.githubreposearch.api.SearchParams
import io.samborskii.githubreposearch.api.entity.Repository
import io.samborskii.githubreposearch.api.entity.SearchResponse
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import java.net.HttpURLConnection

class GitHubClientImplTest : BaseMockWebServerTest() {

    private val mapper: ObjectMapper = jacksonObjectMapper()

    private val emptyResponse: SearchResponse = SearchResponse(0, false, emptyList())
    private val singleResponse: SearchResponse = SearchResponse(
        1, false, listOf(
            Repository(1, "test-repo", "http://repo.io", null, null)
        )
    )
    private val errorResponse: String = """
        {
            "message": "Validation Failed",
            "errors": [
                {
                    "resource": "Search",
                    "field": "q",
                    "code": "missing"
                }
            ],
            "documentation_url": "https://developer.github.com/v3/search"
        }
    """.trimIndent()
    private val rateLimitResponse: String = """
        {
            "message": "API rate limit exceeded for IP: ...",
            "documentation_url": "https://developer.github.com/v3/#rate-limiting"
        }
    """.trimIndent()

    private lateinit var client: GitHubClient

    @BeforeEach
    fun setup() {
        client = GitHubClientImpl(resolve("/"), OkHttpClient(), mapper)
    }

    @Test
    fun `request empty data`() {
        client.searchRepositories(SearchParams("empty", 1, 100))
            .test()
            .assertValue { it == emptyResponse }
    }

    @Test
    fun `request single data`() {
        client.searchRepositories(SearchParams("single", 1, 100))
            .test()
            .assertValue { it == singleResponse }
    }

    @Test
    fun `rate limit request`() {
        client.searchRepositories(SearchParams("rate", 1, 100))
            .test()
            .assertError { it is HttpException && it.code() == 403 }
    }

    override fun response(request: RecordedRequest): MockResponse = when {
        request.path.startsWith("/search/repositories") && "q=empty" in request.path -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(mapper.writeValueAsString(emptyResponse))

        request.path.startsWith("/search/repositories") && "q=single" in request.path -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(mapper.writeValueAsString(singleResponse))

        request.path.startsWith("/search/repositories") && "q=error" in request.path -> MockResponse()
            .setResponseCode(422)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(errorResponse)

        request.path.startsWith("/search/repositories") && "q=rate" in request.path -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(rateLimitResponse)

        else -> notFound()
    }
}

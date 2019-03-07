package io.samborskii.githubreposearch.api

import io.reactivex.Single
import io.samborskii.githubreposearch.api.entity.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET("/search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Single<SearchResponse>
}

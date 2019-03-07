package io.samborskii.githubreposearch.api

import io.reactivex.Single
import io.samborskii.githubreposearch.api.entity.SearchResponse

interface GitHubClient {

    fun searchRepositories(searchParams: SearchParams): Single<SearchResponse>
}

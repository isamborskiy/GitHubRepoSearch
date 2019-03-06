package io.samborskii.githubreposearch.api.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import io.samborskii.githubreposearch.api.GitHubApi
import io.samborskii.githubreposearch.api.GitHubClient
import io.samborskii.githubreposearch.api.entity.SearchResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class GitHubClientImpl(
    hostUrl: String, okHttpClient: OkHttpClient, mapper: ObjectMapper
) : GitHubClient {

    private val api: GitHubApi = Retrofit.Builder()
        .baseUrl(hostUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .client(okHttpClient)
        .build()
        .create(GitHubApi::class.java)

    override fun searchRepositories(keywords: List<String>, page: Int): Single<SearchResponse> =
        api.searchRepositories(keywords.joinToString("+"), page)
}

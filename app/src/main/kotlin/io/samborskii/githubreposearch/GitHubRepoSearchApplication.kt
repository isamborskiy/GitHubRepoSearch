package io.samborskii.githubreposearch

import android.app.Application
import io.samborskii.githubreposearch.api.ApiModule
import io.samborskii.githubreposearch.net.NetModule

class GitHubRepoSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .appModule(AppModule())
            .netModule(NetModule())
            .apiModule(ApiModule())
            .build()
    }
}

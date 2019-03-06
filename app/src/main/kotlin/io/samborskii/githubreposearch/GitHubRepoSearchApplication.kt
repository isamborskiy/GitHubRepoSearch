package io.samborskii.githubreposearch

import android.app.Application
import android.content.Context
import io.samborskii.githubreposearch.api.ApiModule
import io.samborskii.githubreposearch.net.NetModule

class GitHubRepoSearchApplication : Application() {

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .netModule(NetModule())
            .apiModule(ApiModule())
            .build()
    }

    companion object {
        fun getComponent(context: Context): AppComponent {
            val application = context.applicationContext as GitHubRepoSearchApplication
            return application.component
        }
    }
}

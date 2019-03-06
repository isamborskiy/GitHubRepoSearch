package io.samborskii.githubreposearch

import dagger.Component
import io.samborskii.githubreposearch.api.ApiModule
import io.samborskii.githubreposearch.net.NetModule
import io.samborskii.githubreposearch.ui.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetModule::class, ApiModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
}

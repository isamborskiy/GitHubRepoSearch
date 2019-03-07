package io.samborskii.githubreposearch.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.samborskii.githubreposearch.GitHubRepoSearchApplication
import io.samborskii.githubreposearch.R
import io.samborskii.githubreposearch.api.GitHubClient
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var client: GitHubClient

    @JvmField
    @field:[Inject Named("pageSize")]
    var pageSize: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GitHubRepoSearchApplication.getComponent(this).inject(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = RepositoriesAdapter(pageSize) { query, pageNum, pageSize ->
            loadData(query, pageNum, pageSize)
        }

        search.setOnClickListener { (recyclerView.adapter as RepositoriesAdapter).setQuery(searchQuery.text.toString()) }
    }

    @SuppressLint("CheckResult")
    private fun loadData(query: String, pageNum: Int, pageSize: Int) {
        val keywords = query.split(" ")
        client.searchRepositories(keywords, pageNum, pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    (recyclerView.adapter as RepositoriesAdapter).addRepositories(pageNum, it)
                },
                {
                    Snackbar.make(mainLayout, R.string.api_exceeded, Snackbar.LENGTH_SHORT).show()
                }
            )
    }
}

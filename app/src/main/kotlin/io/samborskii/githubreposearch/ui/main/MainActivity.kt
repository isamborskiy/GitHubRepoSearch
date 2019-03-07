package io.samborskii.githubreposearch.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.samborskii.githubreposearch.GitHubRepoSearchApplication
import io.samborskii.githubreposearch.R
import io.samborskii.githubreposearch.api.GitHubClient
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var client: GitHubClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GitHubRepoSearchApplication.getComponent(this).inject(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = RepositoriesAdapter { query, pageNum -> loadData(query, pageNum) }

        search.setOnClickListener { (recyclerView.adapter as RepositoriesAdapter).setQuery(searchQuery.text.toString()) }
    }

    @SuppressLint("CheckResult")
    private fun loadData(query: String, pageNum: Int) {
        val keywords = query.split(" ")
        client.searchRepositories(keywords, pageNum, RepositoriesAdapter.PAGE_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    (recyclerView.adapter as RepositoriesAdapter).addRepositories(pageNum, it)
                },
                {
                    Toast.makeText(this, "API rate limit exceeded", Toast.LENGTH_SHORT).show()
                    Log.e("ERROR", "ERROR", it)
                }
            )
    }
}

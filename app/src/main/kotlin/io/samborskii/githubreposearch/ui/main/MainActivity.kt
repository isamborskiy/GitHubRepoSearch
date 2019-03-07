package io.samborskii.githubreposearch.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.samborskii.githubreposearch.GitHubRepoSearchApplication
import io.samborskii.githubreposearch.R
import io.samborskii.githubreposearch.api.GitHubClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.HttpException
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)

        val adapter = recyclerView.adapter as RepositoriesAdapter
        val searchView = menu?.findItem(R.id.appBarSearch)?.actionView as SearchView?
        searchView?.setOnQueryTextListener(SearchQueryListener(searchView, adapter))
        searchView?.setOnCloseListener {
            adapter.setQuery("")
            false
        }

        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("CheckResult")
    private fun loadData(query: String, pageNum: Int, pageSize: Int) {
        val keywords = query.split(" ")
        client.searchRepositories(keywords, pageNum, pageSize)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { (recyclerView.adapter as RepositoriesAdapter).addRepositories(pageNum, it) },
                {
                    (recyclerView.adapter as RepositoriesAdapter).loadingFailed()

                    if (it is HttpException && it.code() == 403) {
                        Toast.makeText(this, R.string.api_exceeded, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.network_problem, Toast.LENGTH_SHORT).show()
                    }
                }
            )
    }

    private class SearchQueryListener(
        private val searchView: SearchView,
        private val adapter: RepositoriesAdapter
    ) : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let { adapter.setQuery(it) }
            searchView.clearFocus()
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    }
}

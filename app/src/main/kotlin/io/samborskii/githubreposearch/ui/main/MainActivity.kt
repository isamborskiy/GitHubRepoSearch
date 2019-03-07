package io.samborskii.githubreposearch.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.samborskii.githubreposearch.GitHubRepoSearchApplication
import io.samborskii.githubreposearch.R
import io.samborskii.githubreposearch.api.GitHubClient
import io.samborskii.githubreposearch.api.SearchParams
import io.samborskii.githubreposearch.api.entity.Repository
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

    private var disposable: Disposable? = null

    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GitHubRepoSearchApplication.getComponent(this).inject(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter =
            RepositoriesAdapter(pageSize, { loadData(it) }, { query, visible -> emptyData(query, visible) })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)

        val adapter = recyclerView.adapter as RepositoriesAdapter
        searchView = (menu?.findItem(R.id.appBarSearch)?.actionView as SearchView?)?.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { adapter.setQuery(it) }
                    this@apply.clearFocus()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

            setOnCloseListener {
                adapter.setQuery("")
                false
            }

            setQuery(adapter.getQuery(), false)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val query = savedInstanceState?.getString(QUERY_KEY)
        val repositories = savedInstanceState?.getParcelableArrayList(DATA_KEY) ?: emptyList<Repository>()

        val adapter = recyclerView.adapter as RepositoriesAdapter
        adapter.onRestoreInstanceState(query, repositories)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val adapter = recyclerView.adapter as RepositoriesAdapter
        outState?.putString(QUERY_KEY, adapter.getQuery())
        outState?.putParcelableArrayList(DATA_KEY, adapter.getRepositories())

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        searchView?.apply {
            this.setOnQueryTextListener(null)
            this.setOnCloseListener(null)
        }
        disposable?.dispose()
        super.onDestroy()
    }

    private fun emptyData(query: String?, visible: Boolean) {
        emptyData.visibility = if (visible) View.VISIBLE else View.GONE

        if (query?.isNotBlank() == true) {
            emptyData.text = getString(R.string.cannot_find_any_repository, query)
        } else {
            emptyData.text = getString(R.string.empty_data)
        }
    }

    private fun loadData(searchParams: SearchParams) {
        disposable = client.searchRepositories(searchParams)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { (recyclerView.adapter as RepositoriesAdapter).addRepositories(searchParams.pageNum, it) },
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

    companion object {
        private const val QUERY_KEY: String = "QUERY_KEY"
        private const val DATA_KEY: String = "DATA_KEY"
    }
}

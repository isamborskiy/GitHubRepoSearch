package io.samborskii.githubreposearch.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = RepositoriesAdapter()

        search.setOnClickListener {
            val query = searchQuery.text.split(" ")
            client.searchRepositories(query, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { (recyclerView.adapter as RepositoriesAdapter).updateRepositories(it.items) },
                    {
                        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                        Log.e("ERROR", "ERROR", it)
                    }
                )
        }
    }
}

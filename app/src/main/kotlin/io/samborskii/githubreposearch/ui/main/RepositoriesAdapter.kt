package io.samborskii.githubreposearch.ui.main

import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.samborskii.githubreposearch.R
import io.samborskii.githubreposearch.api.entity.EMPTY_REPOSITORY
import io.samborskii.githubreposearch.api.entity.Repository
import io.samborskii.githubreposearch.api.entity.SearchResponse
import kotlinx.android.synthetic.main.list_item_repository.view.*


/**
 * @param loadingCallback calls when loading item becomes visible (provides query, page index and page size for loading)
 */
class RepositoriesAdapter(
    private val pageSize: Int,
    private val loadingCallback: (String, Int, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val repositories: MutableList<Repository> = mutableListOf()
    private var query: String? = null

    fun setQuery(query: String) {
        if (this.query != query) {
            val size = repositories.size
            this.repositories.clear()
            this.query = null
            notifyItemRangeRemoved(0, size)

            if (query.isNotBlank()) {
                this.query = query
                this.repositories += EMPTY_REPOSITORY
                notifyItemInserted(0)
            }
        }
    }

    fun addRepositories(pageNum: Int, page: SearchResponse) {
        this.repositories.removeAt(this.repositories.size - 1) // remove loading item
        notifyItemRemoved(this.repositories.size)

        val start = this.repositories.size % pageSize
        val newRepos = page.items.subList(start, page.items.size)

        this.repositories += newRepos
        if (this.repositories.size < page.totalCount) this.repositories += EMPTY_REPOSITORY

        val firstInsertedItemIndex = (pageNum - 1) * pageSize + start
        notifyItemRangeInserted(firstInsertedItemIndex, this.repositories.size - firstInsertedItemIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (type) {
            ITEM_TYPE -> {
                val view = inflater.inflate(R.layout.list_item_repository, parent, false)
                RepositoryViewHolder(view)
            }
            // LOADING_TYPE
            else -> {
                val view = inflater.inflate(R.layout.list_item_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(view: RecyclerView.ViewHolder, index: Int) {
        if (getItemViewType(index) == ITEM_TYPE) {
            (view as RepositoryViewHolder).bind(repositories[index])
        } else {
            query?.let {
                loadingCallback(it, repositories.size / pageSize + 1, pageSize)
            }
        }
    }

    override fun getItemCount(): Int = repositories.size

    override fun getItemViewType(position: Int): Int =
        if (repositories[position] == EMPTY_REPOSITORY) LOADING_TYPE else ITEM_TYPE

    companion object {
        private const val ITEM_TYPE: Int = 0
        private const val LOADING_TYPE: Int = 1
    }
}

class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(repository: Repository) {
        itemView.repoName.text = repository.fullName
        itemView.repoDescription.setTextOrHide(repository.description)
        itemView.repoLanguage.setTextOrHide(repository.language)

        itemView.repoCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(repository.htmlUrl)
            }
            startActivity(itemView.context, intent, null)
        }
    }

    private fun TextView.setTextOrHide(text: String?) {
        if (text != null) {
            this.visibility = View.VISIBLE
            this.text = text
        } else {
            this.visibility = View.GONE
        }
    }
}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

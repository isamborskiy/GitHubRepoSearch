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
import io.samborskii.githubreposearch.api.entity.Repository
import kotlinx.android.synthetic.main.list_item_repository.view.*


class RepositoriesAdapter(
    private val repositories: MutableList<Repository> = mutableListOf()
) : RecyclerView.Adapter<RepositoryViewHolder>() {

    fun updateRepositories(repositories: List<Repository>) {
        this.repositories.clear()
        this.repositories += repositories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): RepositoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_repository, parent, false)
        return RepositoryViewHolder(view)
    }

    override fun getItemCount(): Int = repositories.size

    override fun onBindViewHolder(view: RepositoryViewHolder, index: Int) {
        view.bind(repositories[index])
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

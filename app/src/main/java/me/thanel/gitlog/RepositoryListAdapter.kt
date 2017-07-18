package me.thanel.gitlog

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryListAdapter : RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {
    private val repositories = mutableListOf<Repository>()

    fun addAll(items: Iterable<Repository>) {
        val positionStart = repositories.size
        repositories.addAll(items)
        notifyItemRangeInserted(positionStart, repositories.size - positionStart)
    }

    fun add(repository: Repository) {
        repositories.add(repository)
        notifyItemInserted(repositories.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = repositories[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_repository))
    }

    override fun getItemCount(): Int {
        return repositories.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val repositoryNameView: TextView by lazy { itemView.repositoryNameView }

        init {
            itemView.setOnClickListener {
                val repository = it.tag as Repository
                val intent = RepositoryActivity.newIntent(itemView.context, repository)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(item: Repository) {
            itemView.tag = item
            repositoryNameView.text = item.name
        }
    }
}
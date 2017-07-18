package me.thanel.gitlog

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_repository.view.*

class RepositoryListAdapter : RecyclerView.Adapter<RepositoryListAdapter.ViewHolder>() {
    private val repositories = mutableListOf<Repository>()

    private var onOpenRepository: (Repository) -> Unit = {}

    fun setOnOpenRepositoryListener(listener: (Repository) -> Unit) {
        onOpenRepository = listener
    }

    fun addAll(items: Iterable<Repository>) {
        val positionStart = repositories.size
        repositories.addAll(items)
        notifyItemRangeInserted(positionStart, repositories.size - positionStart)
    }

    fun add(repository: Repository) {
        repositories.add(repository)
        notifyItemInserted(repositories.size)
    }

    fun remove(repository: Repository) {
        val index = repositories.indexOf(repository)
        if (index >= 0) {
            repositories.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = repositories[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_repository), onOpenRepository)
    }

    override fun getItemCount(): Int {
        return repositories.size
    }

    class ViewHolder(
            itemView: View,
            onOpenRepository: (Repository) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val repositoryNameView: TextView by lazy { itemView.repositoryNameView }

        init {
            itemView.setOnClickListener {
                val repository = it.tag as Repository
                onOpenRepository(repository)
            }
        }

        fun bind(item: Repository) {
            itemView.tag = item
            repositoryNameView.text = item.name
        }
    }
}
package me.thanel.gitlog.repositorylist

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_repository.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.db.Repository
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.inflate

class RepositoryListAdapter : ItemAdapter<Repository, RepositoryListAdapter.ViewHolder>() {
    private var onOpenRepository: (Repository) -> Unit = {}

    fun setOnOpenRepositoryListener(listener: (Repository) -> Unit) {
        onOpenRepository = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_repository), onOpenRepository)

    class ViewHolder(
            itemView: View,
            onOpenRepository: (Repository) -> Unit
    ) : ItemAdapter.ViewHolder<Repository>(itemView) {
        private val repositoryNameView: TextView by lazy { itemView.repositoryNameView }

        init {
            itemView.setOnClickListener {
                val repository = it.tag as Repository
                onOpenRepository(repository)
            }
        }

        override fun bind(item: Repository) {
            itemView.tag = item
            repositoryNameView.text = item.name
        }
    }
}
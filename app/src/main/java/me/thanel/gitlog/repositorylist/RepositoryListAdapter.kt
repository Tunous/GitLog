package me.thanel.gitlog.repositorylist

import android.view.View
import kotlinx.android.synthetic.main.item_repository.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.db.model.Repository

class RepositoryListAdapter(
    onItemClickListener: (Repository) -> Unit
) : ItemAdapter<Repository, RepositoryListAdapter.ViewHolder>(onItemClickListener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getLayoutResId(viewType: Int) = R.layout.item_repository

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<Repository>(itemView) {
        private val repositoryNameView = itemView.repositoryNameView

        override fun bind(item: Repository) {
            super.bind(item)
            repositoryNameView.text = item.name
        }
    }
}

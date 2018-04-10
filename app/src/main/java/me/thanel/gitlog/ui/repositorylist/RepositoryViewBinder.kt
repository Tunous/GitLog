package me.thanel.gitlog.ui.repositorylist

import kotlinx.android.synthetic.main.item_repository.*
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.GitLogRepository
import me.thanel.gitlog.ui.base.adapter.ContainerViewBinder
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder

class RepositoryViewBinder(
    private val onItemClickListener: (GitLogRepository) -> Unit
) : ContainerViewBinder<GitLogRepository>() {
    override val layoutResource: Int
        get() = R.layout.item_repository

    override fun onBindViewHolder(holder: ContainerViewHolder, item: GitLogRepository) {
        holder.repositoryNameView.text = item.name
        holder.containerView.setOnClickListener {
            onItemClickListener(item)
        }
    }
}

package me.thanel.gitlog.ui.repository.branchlist

import kotlinx.android.synthetic.main.item_branch.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ContainerViewBinder
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

class RefViewBinder(
    private val onItemClickListener: (Ref) -> Unit
) : ContainerViewBinder<Ref>() {
    override val layoutResource: Int
        get() = R.layout.item_branch

    override fun onBindViewHolder(holder: ContainerViewHolder, item: Ref) {
        holder.branchNameView.text = Repository.shortenRefName(item.name)
        holder.containerView.setOnClickListener {
            onItemClickListener(item)
        }
    }
}

package me.thanel.gitlog.commit

import android.view.View
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.view.CommitDetailsView
import org.eclipse.jgit.revwalk.RevCommit

class CommitDetailsViewHolder(itemView: View) : ItemAdapter.ViewHolder<RevCommit>(itemView) {
    private val commitDetailsView = itemView as CommitDetailsView

    override fun bind(item: RevCommit) {
        super.bind(item)
        commitDetailsView.bindCommit(item)
    }
}
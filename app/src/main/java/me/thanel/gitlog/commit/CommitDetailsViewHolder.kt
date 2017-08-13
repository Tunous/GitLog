package me.thanel.gitlog.commit

import android.view.View
import kotlinx.android.synthetic.main.item_commit_details.view.*
import me.thanel.gitlog.base.ItemAdapter
import org.eclipse.jgit.revwalk.RevCommit

class CommitDetailsViewHolder(itemView: View) : ItemAdapter.ViewHolder<RevCommit>(itemView) {
    private val commitMessageView = itemView.commitMessageView

    override fun bind(item: RevCommit) {
        super.bind(item)
        commitMessageView.text = item.fullMessage.trim()
    }
}
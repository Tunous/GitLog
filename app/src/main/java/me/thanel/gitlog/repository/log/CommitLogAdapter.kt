package me.thanel.gitlog.repository.log

import android.view.View
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.view.AvatarDrawable
import me.thanel.gitlog.view.SmallCircleDrawable
import org.eclipse.jgit.revwalk.RevCommit

class CommitLogAdapter(
        onItemClickListener: (RevCommit) -> Unit
) : ItemAdapter<RevCommit, CommitLogAdapter.ViewHolder>(onItemClickListener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getLayoutResId(viewType: Int) = R.layout.item_log

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<RevCommit>(itemView) {
        private val logMessage =  itemView.logMessage
        private val avatarView =  itemView.commitAuthorAvatarView
        private val detailsIndicator =  itemView.commitDetailsIndicator

        override fun bind(item: RevCommit) {
            super.bind(item)
            logMessage.text = item.shortMessage

            if (item.parentCount > 1) {
                avatarView.setImageDrawable(SmallCircleDrawable())
            } else {
                avatarView.setImageDrawable(AvatarDrawable(item.authorIdent.name, item.authorIdent))
            }

            detailsIndicator.visibility = if (item.fullMessage.trim().split("\n").size > 1) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}

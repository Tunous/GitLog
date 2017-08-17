package me.thanel.gitlog.repository.log

import android.view.View
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.isVisible
import org.eclipse.jgit.lib.AnyObjectId
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit

class CommitLogAdapter(
        onItemClickListener: (RevCommit) -> Unit
) : ItemAdapter<RevCommit, CommitLogAdapter.ViewHolder>(onItemClickListener) {
    private val refs = mutableMapOf<AnyObjectId, MutableSet<Ref>>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getLayoutResId(viewType: Int) = R.layout.item_log

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView, refs)

    fun replaceRefs(newRefs: MutableMap<AnyObjectId, MutableSet<Ref>>) {
        refs.clear()
        refs.putAll(newRefs)
        notifyDataSetChanged()
    }

    class ViewHolder(
            itemView: View,
            private val refs: MutableMap<AnyObjectId, MutableSet<Ref>>
    ) : ItemAdapter.ViewHolder<RevCommit>(itemView) {
        private val logMessage = itemView.logMessageView
        private val avatarView = itemView.avatarView
        private val detailsIndicator = itemView.commitDetailsIndicator
        private val branchView = itemView.branchView

        override fun bind(item: RevCommit) {
            super.bind(item)

            val matchingRefs = refs[item]
            if (matchingRefs != null) {
                val name = matchingRefs.joinToString { Repository.shortenRefName(it.name) }
                branchView.text = name
                branchView.isVisible = true
            } else {
                branchView.isVisible = false
            }

            logMessage.text = item.shortMessage
            avatarView.setFromCommit(item, allowMergeIndicator = true)
            detailsIndicator.isVisible = item.fullMessage.trim().split("\n").size > 1
        }
    }
}

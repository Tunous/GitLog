package me.thanel.gitlog

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_log.view.*
import org.eclipse.jgit.revwalk.RevCommit

class CommitLogAdapter : RecyclerView.Adapter<CommitLogAdapter.ViewHolder>() {
    private val commits = mutableListOf<RevCommit>()

    fun addAll(items: Iterable<RevCommit>) {
        val positionStart = commits.size
        commits.addAll(items)
        notifyItemRangeInserted(positionStart, commits.size - positionStart)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commits[position])
    }

    override fun getItemCount(): Int {
        return commits.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_log))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val logMessage: TextView by lazy { itemView.logMessage }
        private val avatarView: ImageView by lazy { itemView.commitAuthorAvatarView }

        init {
            itemView.setOnClickListener {
                val commit = it.tag as RevCommit
                val intent = CommitActivity.newIntent(itemView.context, Commit(commit))
                itemView.context.startActivity(intent)
            }
        }

        fun bind(item: RevCommit) {
            itemView.tag = item
            logMessage.text = item.shortMessage

            if (item.parentCount > 1) {
                avatarView.setImageDrawable(SmallCircleDrawable())
            } else {
                avatarView.setImageDrawable(AvatarDrawable(item.authorIdent.name, item.authorIdent))
            }
        }
    }
}
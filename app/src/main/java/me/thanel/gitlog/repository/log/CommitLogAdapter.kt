package me.thanel.gitlog.repository.log

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.Repo
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.model.Commit
import me.thanel.gitlog.utils.inflate
import me.thanel.gitlog.view.AvatarDrawable
import me.thanel.gitlog.view.SmallCircleDrawable
import org.eclipse.jgit.revwalk.RevCommit

class CommitLogAdapter(
        private val repository: Repo
) : RecyclerView.Adapter<CommitLogAdapter.ViewHolder>() {
    private val commits = mutableListOf<RevCommit>()

    fun addAll(items: Iterable<RevCommit>) {
        val positionStart = commits.size
        commits.addAll(items)
        notifyItemRangeInserted(positionStart, commits.size - positionStart)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(commits[position])

    override fun getItemCount() = commits.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_log), repository)

    class ViewHolder(
            itemView: View,
            repository: Repo
    ) : RecyclerView.ViewHolder(itemView) {
        private val logMessage: TextView by lazy { itemView.logMessage }
        private val avatarView: ImageView by lazy { itemView.commitAuthorAvatarView }

        init {
            itemView.setOnClickListener {
                val commit = it.tag as RevCommit
                val intent = CommitActivity.newIntent(itemView.context, Commit(commit), repository)
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
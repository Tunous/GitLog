package me.thanel.gitlog.ui.repository.log

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import androidx.view.isVisible
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.utils.inflate
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotLane

class CommitLogAdapter(
    private val plotCommitList: ColorCommitList,
    private val onItemClickListener: (PlotCommit<PlotLane>) -> Unit
) : RecyclerView.Adapter<CommitLogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item_log).apply {
            setOnClickListener {
                @Suppress("UNCHECKED_CAST")
                val commit = it.tag as PlotCommit<PlotLane>
                onItemClickListener(commit)
            }
        })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(plotCommitList[position])

    override fun getItemCount() = plotCommitList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val logMessage = itemView.logMessageView
        private val detailsIndicator = itemView.commitDetailsIndicator
        private val branchView = itemView.branchView
        private val laneView = itemView.laneView

        fun bind(commit: PlotCommit<ColorCommitList.ColorLane>) {
            itemView.tag = commit
            laneView.commit = commit
            markRefs(commit)
            logMessage.text = commit.shortMessage
            detailsIndicator.isVisible = commit.fullMessage.trim().split("\n").size > 1
            detailsIndicator.setOnClickListener {
                logMessage.text =
                        if (logMessage.text == commit.shortMessage) commit.fullMessage else commit.shortMessage
            }
        }

        private fun markRefs(commit: PlotCommit<*>) {
            if (commit.refCount > 0) {
                val refNames = (0 until commit.refCount)
                    .map(commit::getRef)
                    .filterNot { it.isSymbolic }
                    .joinToString { Repository.shortenRefName(it.name) }
                branchView.text = refNames
                branchView.isVisible = true
            } else {
                branchView.isVisible = false
            }
        }
    }
}

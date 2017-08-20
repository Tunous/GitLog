package me.thanel.gitlog.repository.log

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.inflate
import me.thanel.gitlog.utils.isVisible
import me.thanel.gitlog.utils.loadAvatar
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane

class CommitLogAdapter(
        private val plotCommitList: PlotCommitList<PlotLane>,
        private val onItemClickListener: (PlotCommit<PlotLane>) -> Unit
) : RecyclerView.Adapter<CommitLogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item_log).apply {
            setOnClickListener {
                @Suppress("UNCHECKED_CAST")
                val commit = it.tag as PlotCommit<PlotLane>
                onItemClickListener(commit)
            }
        }, plotCommitList)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(plotCommitList[position])

    override fun getItemCount() = plotCommitList.size

    class ViewHolder(
            itemView: View,
            private val plotCommitList: PlotCommitList<PlotLane>
    ) : RecyclerView.ViewHolder(itemView) {
        private val logMessage = itemView.logMessageView
        private val detailsIndicator = itemView.commitDetailsIndicator
        private val branchView = itemView.branchView
        private val laneView = itemView.laneView

        fun bind(commit: PlotCommit<PlotLane>) {
            itemView.tag = commit

            bindLaneView(commit)
            markRefs(commit)

            logMessage.text = commit.shortMessage
            detailsIndicator.isVisible = commit.fullMessage.trim().split("\n").size > 1
        }

        private fun bindLaneView(commit: PlotCommit<PlotLane>) {
            val committerIdent = commit.committerIdent
            if (committerIdent != null) {
                itemView.context.loadAvatar(committerIdent.emailAddress, laneView)
            } else {
                Picasso.with(itemView.context).cancelRequest(laneView)
                laneView.setImageDrawable(null)
            }

            laneView.mainLane = commit.lane.position
            laneView.clearLanes()

            val passing = mutableListOf<PlotLane>()
            plotCommitList.findPassingThrough(commit, passing)
            laneView.setPassing(passing)

            for (i in 0 until commit.childCount) {
                laneView.addChildLane(commit.getChild(i).lane.position)
            }
            for (i in 0 until commit.parentCount) {
                laneView.addParentLane((commit.getParent(i) as PlotCommit<*>).lane.position)
            }

            laneView.invalidate()
            laneView.requestLayout()
        }

        private fun markRefs(commit: PlotCommit<PlotLane>) {
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

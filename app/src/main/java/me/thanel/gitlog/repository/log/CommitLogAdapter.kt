package me.thanel.gitlog.repository.log

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.inflate
import me.thanel.gitlog.utils.isVisible
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane

class CommitLogAdapter(
        context: Context,
        private val plotCommitList: PlotCommitList<PlotLane>,
        private val onItemClickListener: (PlotCommit<PlotLane>) -> Unit
) : RecyclerView.Adapter<CommitLogAdapter.ViewHolder>() {
    private val lanesWidth: Int
    init {
        val lanes = plotCommitList.maxBy { it.lane.position }?.lane?.position ?: 0
        lanesWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                ((lanes + 1) * 6 + 4).toFloat(), context.resources.displayMetrics).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(parent.inflate(R.layout.item_log).apply {
            setOnClickListener {
                @Suppress("UNCHECKED_CAST")
                val commit = it.tag as PlotCommit<PlotLane>
                onItemClickListener(commit)
            }
        }, lanesWidth, plotCommitList)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(plotCommitList[position])

    override fun getItemCount() = plotCommitList.size

    class ViewHolder(
            itemView: View,
            lanesWidth: Int,
            private val plotCommitList: PlotCommitList<PlotLane>
    ) : RecyclerView.ViewHolder(itemView) {
        private val logMessage = itemView.logMessageView
        private val avatarView = itemView.avatarView
        private val detailsIndicator = itemView.commitDetailsIndicator
        private val branchView = itemView.branchView
        private val laneView = itemView.laneView.apply {
            layoutParams = layoutParams.apply {
                width = lanesWidth
            }
        }

        fun bind(item: PlotCommit<PlotLane>) {
            itemView.tag = item

            laneView.mainLane = item.lane.position
            laneView.clearLines()

            val passing = mutableListOf<PlotLane>()
            plotCommitList.findPassingThrough(item, passing)
            laneView.setPassing(passing)

            for (i in 0 until item.childCount) {
                laneView.addChildLane(item.getChild(i).lane.position)
            }
            for (i in 0 until item.parentCount) {
                laneView.addParentLane((item.getParent(i) as PlotCommit<*>).lane.position)
            }

            branchView.isVisible = false

            logMessage.text = item.shortMessage
            avatarView.setFromCommit(item, allowMergeIndicator = true)
            detailsIndicator.isVisible = item.fullMessage.trim().split("\n").size > 1
        }

//        private fun markBranches() {
//            val matchingRefs = refs[item]
//            if (matchingRefs != null) {
//                val name = matchingRefs.joinToString { Repository.shortenRefName(it.name) }
//                branchView.text = name
//                branchView.isVisible = true
//            } else {
//                branchView.isVisible = false
//            }
//        }
    }
}

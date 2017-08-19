package me.thanel.gitlog.repository.log

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_log.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.dpToPx
import me.thanel.gitlog.utils.inflate
import me.thanel.gitlog.utils.isVisible
import me.thanel.gitlog.utils.md5
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
        lanesWidth = context.dpToPx(((lanes + 1) * 24 + 4).toFloat()).toInt()
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
        private val laneView = itemView.laneView

        fun bind(item: PlotCommit<PlotLane>) {
            itemView.tag = item

            laneView.tag = item.shortMessage

            // TODO: Extract to method
            val GRAVATAR_URL = "https://www.gravatar.com/avatar"
            val hash = item.authorIdent.emailAddress.trim().toLowerCase().md5()
            val url = Uri.parse(GRAVATAR_URL).buildUpon()
                    .appendPath(hash)
                    .appendQueryParameter("d", "identicon")
                    .toString()

            Picasso.with(itemView.context)
                    .load(url)
                    .into(laneView)

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
            laneView.invalidate()
            laneView.requestLayout()

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

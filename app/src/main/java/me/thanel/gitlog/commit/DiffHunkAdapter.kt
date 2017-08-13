package me.thanel.gitlog.commit

import android.support.v7.widget.RecyclerView
import android.view.View
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.revwalk.RevCommit

class DiffHunkAdapter(
        private val viewModel: CommitViewModel
) : ItemAdapter<DiffEntry, RecyclerView.ViewHolder>() {
    var commit: RevCommit? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = super.getItemCount() + HEADER_VIEWS

    override fun getItemViewType(position: Int) = when (position) {
        0 -> VIEW_TYPE_COMMIT_DETAILS
        1 -> VIEW_TYPE_SUMMARY
        else -> super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_COMMIT_DETAILS -> {
                commit?.let { (holder as CommitDetailsViewHolder).bind(it) }
            }
            VIEW_TYPE_SUMMARY -> {
                (holder as DiffSummaryViewHolder).bind(items)
            }
            else -> (holder as DiffHunkViewHolder).bind(items[position - HEADER_VIEWS])
        }
    }

    override fun getLayoutResId(viewType: Int): Int {
        return when (viewType) {
            VIEW_TYPE_COMMIT_DETAILS -> R.layout.item_commit_details
            VIEW_TYPE_SUMMARY -> R.layout.item_changes_summary
            else -> R.layout.item_diff_hunk
        }
    }

    override fun createViewHolder(itemView: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_COMMIT_DETAILS -> CommitDetailsViewHolder(itemView, viewModel.repositoryId)
            VIEW_TYPE_SUMMARY -> DiffSummaryViewHolder(itemView, viewModel)
            else -> DiffHunkViewHolder(itemView, viewModel)
        }
    }

    companion object {
        private const val VIEW_TYPE_COMMIT_DETAILS = 1
        private const val VIEW_TYPE_SUMMARY = 2

        private const val HEADER_VIEWS = 2
    }
}


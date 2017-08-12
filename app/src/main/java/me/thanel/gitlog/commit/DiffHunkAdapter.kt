package me.thanel.gitlog.commit

import android.view.View
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.view.DiffHunkView
import org.eclipse.jgit.diff.DiffEntry

class DiffHunkAdapter(
        private val viewModel: CommitViewModel
) : ItemAdapter<DiffEntry, DiffHunkAdapter.ViewHolder>() {
    override fun getLayoutResId(viewType: Int): Int = R.layout.item_diff_hunk

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView, viewModel)

    class ViewHolder(
            itemView: View,
            private val viewModel: CommitViewModel
    ) : ItemAdapter.ViewHolder<DiffEntry>(itemView) {
        private val diffHunkView by lazy { itemView.findViewById<DiffHunkView>(R.id.diffHunkView) }

        override fun bind(item: DiffEntry) {
            super.bind(item)

            diffHunkView.setDiff(viewModel.formatDiffEntry(item))
        }
    }
}
package me.thanel.gitlog.commit

import android.graphics.Color
import android.view.View
import android.widget.TextView
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
        private val fileNameView by lazy { itemView.findViewById<TextView>(R.id.fileNameView) }
        private val diffHunkView by lazy { itemView.findViewById<DiffHunkView>(R.id.diffHunkView) }

        init {
            fileNameView.setOnClickListener {
                diffHunkView.visibility = if (diffHunkView.visibility == View.VISIBLE) View.GONE
                else View.VISIBLE
            }
        }

        override fun bind(item: DiffEntry) {
            super.bind(item)

            fileNameView.text = when (item.changeType) {
                DiffEntry.ChangeType.ADD -> item.newPath
                DiffEntry.ChangeType.RENAME,
                DiffEntry.ChangeType.COPY -> "${item.oldPath} -> ${item.newPath}"
                else -> item.oldPath
            }

            val color = when (item.changeType) {
                DiffEntry.ChangeType.ADD -> Color.GREEN
                DiffEntry.ChangeType.DELETE -> Color.RED
                DiffEntry.ChangeType.RENAME,
                DiffEntry.ChangeType.COPY -> Color.BLUE
                else -> Color.BLACK
            }
            fileNameView.setTextColor(color)
            diffHunkView.setDiff(viewModel.formatDiffEntry(item))
            diffHunkView.visibility = View.GONE
        }
    }
}
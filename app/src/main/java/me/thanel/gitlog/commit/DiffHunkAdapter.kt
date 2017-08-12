package me.thanel.gitlog.commit

import android.graphics.Color
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.View
import android.widget.ImageView
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
        private val fileNameView = itemView.findViewById<TextView>(R.id.fileNameView)
        private val diffHunkView = itemView.findViewById<DiffHunkView>(R.id.diffHunkView)
        private val actionsMenuButton = itemView.findViewById<ImageView>(R.id.actionsMenu)
        private val actionsMenu = PopupMenu(context, actionsMenuButton, Gravity.END or Gravity.TOP)

        init {
            fileNameView.setOnClickListener {
                diffHunkView.visibility = if (diffHunkView.visibility == View.VISIBLE) View.GONE
                else View.VISIBLE
                val isVisible = diffHunkView.visibility == View.VISIBLE
                val arrowResId = if (isVisible) R.drawable.ic_arrow_drop_up
                else R.drawable.ic_arrow_drop_down
                fileNameView.setCompoundDrawablesWithIntrinsicBounds(arrowResId, 0, 0, 0)
            }

            actionsMenu.inflate(R.menu.diff_hunk)

            val listener: (View) -> Boolean = {
                actionsMenu.show()
                true
            }
            fileNameView.setOnLongClickListener(listener)
            actionsMenuButton.setOnLongClickListener(listener)
            actionsMenuButton.setOnClickListener {
                actionsMenu.show()
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
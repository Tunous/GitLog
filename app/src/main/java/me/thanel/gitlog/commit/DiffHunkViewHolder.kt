package me.thanel.gitlog.commit

import android.graphics.Color
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.diff.DiffActivity
import me.thanel.gitlog.view.DiffHunkView
import org.eclipse.jgit.diff.DiffEntry

class DiffHunkViewHolder(
        itemView: View,
        private val viewModel: CommitViewModel
) : ItemAdapter.ViewHolder<DiffEntry>(itemView),
        PopupMenu.OnMenuItemClickListener,
        View.OnLongClickListener,
        View.OnClickListener {
    private val fileNameView = itemView.findViewById<TextView>(R.id.fileNameView)
    private val diffHunkView = itemView.findViewById<DiffHunkView>(R.id.diffHunkView)
    private val actionsMenuButton = itemView.findViewById<ImageView>(R.id.actionsMenu)
    private val actionsMenu = PopupMenu(context, actionsMenuButton,
            Gravity.END or Gravity.TOP).apply {
        inflate(R.menu.diff_hunk)
        setOnMenuItemClickListener(this@DiffHunkViewHolder)
    }

    init {
        fileNameView.setOnClickListener {
            diffHunkView.visibility = if (diffHunkView.visibility == View.VISIBLE) View.GONE
            else View.VISIBLE
            val isVisible = diffHunkView.visibility == View.VISIBLE
            val arrowResId = if (isVisible) R.drawable.ic_arrow_drop_up
            else R.drawable.ic_arrow_drop_down
            fileNameView.setCompoundDrawablesWithIntrinsicBounds(arrowResId, 0, 0, 0)
        }

        fileNameView.setOnLongClickListener(this)
        actionsMenuButton.setOnLongClickListener(this)
        actionsMenuButton.setOnClickListener(this)
    }

    override fun bind(item: DiffEntry) {
        super.bind(item)
        itemView.tag = item

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

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.view_full_screen -> {
                val diffEntry = itemView.tag as DiffEntry
                diffEntry.newId
                val intent = DiffActivity.newIntent(context, viewModel.commitSha,
                        viewModel.repositoryId, diffEntry.newId)
                context.startActivity(intent)
            }
            else -> return false
        }
        return true
    }

    override fun onLongClick(view: View): Boolean {
        actionsMenu.show()
        return true
    }

    override fun onClick(view: View) {
        actionsMenu.show()
    }
}
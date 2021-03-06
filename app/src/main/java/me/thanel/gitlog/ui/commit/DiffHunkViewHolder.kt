package me.thanel.gitlog.ui.commit

import android.graphics.Color
import android.support.transition.ChangeTransform
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_diff_hunk.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.preferences.Preferences
import me.thanel.gitlog.ui.base.adapter.ItemViewHolder
import me.thanel.gitlog.ui.diff.DiffViewerActivityStarter
import me.thanel.gitlog.ui.repository.file.GitFileViewerActivityStarter
import org.eclipse.jgit.diff.DiffEntry

class DiffHunkViewHolder(
    itemView: View,
    private val repositoryId: Int,
    private val commitSha: String
) : ItemViewHolder<FormattedDiffEntry>(itemView),
    PopupMenu.OnMenuItemClickListener,
    View.OnClickListener {
    private val diffHeader = itemView.diffHeader.apply {
        setOnClickListener(this@DiffHunkViewHolder)
    }
    private val fileNameView = itemView.fileNameView
    private val diffHunkView = itemView.diffHunkView
    private val expandDropDown = itemView.expandArrow
    private val actionsMenuButton = itemView.actionsMenu.apply {
        setOnClickListener(this@DiffHunkViewHolder)
    }
    private val actionsMenu = PopupMenu(context, actionsMenuButton).apply {
        inflate(R.menu.diff_hunk)
        setOnMenuItemClickListener(this@DiffHunkViewHolder)
    }

    private val viewWholeFileItem = actionsMenu.menu.findItem(R.id.view_whole_file)

    override fun bind(item: FormattedDiffEntry) {
        super.bind(item)
        itemView.tag = item
        fileNameView.text = getPath(item.diffEntry)
        fileNameView.setTextColor(getColor(item.diffEntry))
        expandDropDown.rotation = 0f

        viewWholeFileItem.isVisible = item.diffEntry.changeType != DiffEntry.ChangeType.DELETE

        diffHunkView.setDiff(item.formattedPatch)
        diffHunkView.setLineNumbersVisible(Preferences.showLineNumbers)
        diffHunkView.visibility = View.GONE
    }

    private fun getColor(item: DiffEntry): Int = when (item.changeType) {
        DiffEntry.ChangeType.ADD -> Color.GREEN
        DiffEntry.ChangeType.DELETE -> Color.RED
        DiffEntry.ChangeType.RENAME,
        DiffEntry.ChangeType.COPY -> Color.BLUE
        else -> Color.BLACK
    }

    private fun getPath(item: DiffEntry): CharSequence? = when (item.changeType) {
        DiffEntry.ChangeType.ADD -> item.newPath
        DiffEntry.ChangeType.RENAME,
        DiffEntry.ChangeType.COPY -> "${item.oldPath} -> ${item.newPath}"
        else -> item.oldPath
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val diffEntry = itemView.tag as FormattedDiffEntry
        when (item.itemId) {
            R.id.view_full_screen -> {
                DiffViewerActivityStarter.start(
                    context,
                    commitSha,
                    repositoryId,
                    diffEntry.diffEntry.newId
                )
            }
            R.id.view_whole_file -> {
                GitFileViewerActivityStarter.start(
                    context,
                    repositoryId,
                    commitSha,
                    diffEntry.diffEntry.newPath
                )
            }
            else -> return false
        }
        return true
    }

    override fun onClick(view: View) {
        when (view) {
            diffHeader -> toggleCodeView()
            actionsMenuButton -> actionsMenu.show()
        }
    }

    private fun toggleCodeView() {
        TransitionManager.beginDelayedTransition(
            itemView.parent as ViewGroup, TransitionSet()
                // Animate only the drop-down, expand animation breaks when scrolling...
                .addTransition(ChangeTransform().addTarget(expandDropDown))
        )

        val wasVisible = diffHunkView.visibility == View.VISIBLE
        diffHunkView.visibility = if (wasVisible) View.GONE else View.VISIBLE
        expandDropDown.rotation = if (wasVisible) 0f else 180f
    }
}

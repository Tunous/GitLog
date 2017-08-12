package me.thanel.gitlog.commit

import android.graphics.Color
import android.view.View
import android.widget.CheckBox
import kotlinx.android.synthetic.main.item_commit_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import org.eclipse.jgit.diff.DiffEntry

class FileAdapter : ItemAdapter<FileEntry, FileAdapter.ViewHolder>() {
    val checkedFiles get() = items.filter { it.isChecked }.map { it.diffEntry }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getLayoutResId(viewType: Int) = R.layout.item_commit_file

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<FileEntry>(itemView) {
        private val fileNameCheckBox: CheckBox by lazy { itemView.fileNameCheckBox }

        override fun bind(item: FileEntry) {
            super.bind(item)
            val diffEntry = item.diffEntry
            fileNameCheckBox.isChecked = item.isChecked

            fileNameCheckBox.text = when (diffEntry.changeType) {
                DiffEntry.ChangeType.ADD -> diffEntry.newPath
                DiffEntry.ChangeType.RENAME,
                DiffEntry.ChangeType.COPY -> "${diffEntry.oldPath} -> ${diffEntry.newPath}"
                else -> diffEntry.oldPath
            }

            fileNameCheckBox.setOnClickListener {
                item.isChecked = fileNameCheckBox.isChecked
            }

            val color = when (diffEntry.changeType) {
                DiffEntry.ChangeType.ADD -> Color.GREEN
                DiffEntry.ChangeType.DELETE -> Color.RED
                DiffEntry.ChangeType.RENAME,
                DiffEntry.ChangeType.COPY -> Color.BLUE
                else -> Color.BLACK
            }
            fileNameCheckBox.setTextColor(color)
        }
    }

    fun selectAll(select: Boolean) {
        for (item in items) {
            item.isChecked = select
        }
        notifyDataSetChanged()
    }
}

data class FileEntry(val diffEntry: DiffEntry, var isChecked: Boolean)
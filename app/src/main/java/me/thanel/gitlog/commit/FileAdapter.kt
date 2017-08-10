package me.thanel.gitlog.commit

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.CheckBox
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import org.eclipse.jgit.diff.DiffEntry

class FileAdapter : ItemAdapter<FileEntry, FileAdapter.ViewHolder>() {

    val checkedFiles get() = items.filter { it.isChecked }.map { it.diffEntry }

    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<FileEntry>(itemView) {
        private val fileNameCheckBox: CheckBox by lazy { itemView.fileNameCheckBox }

        override fun bind(item: FileEntry) {
            super.bind(item)
            val diffEntry = item.diffEntry
            fileNameCheckBox.text = "${diffEntry.oldPath} -> ${diffEntry.newPath}"
            fileNameCheckBox.isChecked = item.isChecked

            fileNameCheckBox.setOnClickListener {
                val isChecked = fileNameCheckBox.isChecked
                Log.d("CommitFragment", "Checked changed $isChecked")
                item.isChecked = isChecked
            }
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